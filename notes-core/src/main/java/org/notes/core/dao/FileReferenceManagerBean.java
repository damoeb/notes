package org.notes.core.dao;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.core.interfaces.FileReferenceManager;
import org.notes.core.model.StandardFileReference;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class FileReferenceManagerBean implements FileReferenceManager {

    private static final Logger LOGGER = Logger.getLogger(FileReferenceManagerBean.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @PostConstruct
    public void onInit() {
        File repository = getRepository();
        if (!repository.exists()) {
            repository.mkdirs();
        }
    }

    @Override
    public File getRepository() {
        return new File(getRepositoryPath());
    }

    private String getRepositoryPath() {
        return System.getProperty("java.io.tmpdir") + File.separator + "notes";
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public StandardFileReference store(FileItem item) throws NotesException {

        try {

            if (item == null) {
                throw new IllegalArgumentException("item is null");
            }

            String checksum = getChecksum(item.getInputStream());

            StandardFileReference reference = find(checksum, item.getSize());

            if (reference != null) {
                return reference;
            }

            reference = new StandardFileReference();

            // store
            File fileInRepo = getNewPath(checksum);
            item.write(fileInRepo);

            String contentType = getContentType(fileInRepo);

            if (contentType == null) {
                throw new IllegalArgumentException(String.format("MimeType %s is not supported", contentType));
            }

            fileInRepo.setExecutable(false);

            reference.setContentType(contentType);
            reference.setChecksum(checksum);
            reference.setSize(item.getSize());
            reference.setReference(fileInRepo.getAbsolutePath());

            em.persist(reference);
            em.flush();


            return reference;

        } catch (Throwable t) {
            throw new NotesException("store failed: " + t.getMessage(), t);
        }
    }

    private String getContentType(File file) throws IOException {
        return Files.probeContentType(Paths.get(file.toURI()));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public StandardFileReference getFileReference(Long fileId) throws NotesException {
        try {
            if (fileId == null || fileId <= 0) {
                throw new NotesException(String.format("Invalid file id '%s'", fileId));
            }

            Query query = em.createNamedQuery(StandardFileReference.QUERY_BY_ID);
            query.setParameter("ID", fileId);

            List<StandardFileReference> fileReferences = query.getResultList();
            if (fileReferences.isEmpty()) {
                throw new NotesException(String.format("No file with id '%s' found", fileId));
            }

            return fileReferences.get(0);

        } catch (Throwable t) {
            throw new NotesException("Cannot load file reference: " + t.getMessage(), t);
        }
    }

    private StandardFileReference find(String checksum, long size) throws NotesException {
        try {

            if (StringUtils.isBlank(checksum)) {
                throw new IllegalArgumentException("checksum is null");
            }

            Query query = em.createNamedQuery(StandardFileReference.QUERY_BY_CHECKSUM);
            query.setParameter("CHECKSUM", checksum);
            query.setParameter("FILESIZE", size);

            List<StandardFileReference> list = (List<StandardFileReference>) query.getResultList();
            if (list.isEmpty()) {
                return null;
            }

            return list.get(0);

        } catch (Throwable t) {
            throw new NotesException(t.getMessage());
        }
    }

    private File getNewPath(String checksum) throws NoSuchAlgorithmException {

        // todo: ensure it does not exist
        return new File(getRepositoryPath() + File.separator + checksum + "." + System.currentTimeMillis() + ".dat");
    }

    private String getChecksum(InputStream stream) throws IOException {
        return DigestUtils.md5Hex(stream);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public StandardFileReference storeTemporary(String pathToSnapshot) throws NotesException {
        try {

            File resource = new File(pathToSnapshot);

            String checksum = getChecksum(new FileInputStream(resource));

            StandardFileReference reference = find(checksum, 0);

            if (reference != null) {
                em.merge(reference); // update date
                return reference;
            }

            reference = new StandardFileReference();

            // store
            File fileInRepo = getNewPath(checksum);
            FileUtils.copyFile(resource, fileInRepo);

            fileInRepo.setExecutable(false);

            reference.setContentType("temp");
            reference.setChecksum(checksum);
            reference.setSize(0);
            reference.setReference(fileInRepo.getAbsolutePath());

            em.persist(reference);
            em.flush();

            return reference;

        } catch (Throwable t) {
            throw new NotesException("store failed: " + t.getMessage(), t);
        }
    }
}
