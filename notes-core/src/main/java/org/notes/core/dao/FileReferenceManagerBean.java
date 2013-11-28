package org.notes.core.dao;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.common.model.ContentType;
import org.notes.common.model.FileReference;
import org.notes.core.interfaces.FileReferenceManager;

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
    public FileReference store(FileItem item) throws NotesException {

        try {

            if (item == null) {
                throw new IllegalArgumentException("item is null");
            }

            String checksum = getChecksum(item.getInputStream());

            FileReference reference = find(checksum, item.getSize());

            if (reference != null) {
                return reference;
            }

            reference = new FileReference();

            // store
            File fileInRepo = getNewPath(checksum);
            item.write(fileInRepo);

            ContentType contentType = getContentType(fileInRepo);

            if (ContentType.UNKNOWN == contentType) {
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

    private ContentType getContentType(File file) throws IOException {
        return ContentType.fromString(Files.probeContentType(Paths.get(file.toURI())));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FileReference getFileReference(Long fileId) throws NotesException {
        try {
            if (fileId == null || fileId <= 0) {
                throw new NotesException(String.format("Invalid file id '%s'", fileId));
            }

            Query query = em.createNamedQuery(FileReference.QUERY_BY_ID);
            query.setParameter("ID", fileId);

            List<FileReference> fileReferences = query.getResultList();
            if (fileReferences.isEmpty()) {
                throw new NotesException(String.format("No file with id '%s' found", fileId));
            }

            return fileReferences.get(0);

        } catch (Throwable t) {
            throw new NotesException("Cannot load file reference: " + t.getMessage(), t);
        }
    }

    private FileReference find(String checksum, long size) throws NotesException {
        try {

            if (StringUtils.isBlank(checksum)) {
                throw new IllegalArgumentException("checksum is null");
            }

            Query query = em.createNamedQuery(FileReference.QUERY_BY_CHECKSUM);
            query.setParameter("CHECKSUM", checksum);
            query.setParameter("FILESIZE", size);

            List<FileReference> list = (List<FileReference>) query.getResultList();
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
    public FileReference storeTemporary(String pathToSnapshot) throws NotesException {
        try {

            File resource = new File(pathToSnapshot);

            String checksum = getChecksum(new FileInputStream(resource));

            FileReference reference = find(checksum, 0);

            if (reference != null) {
                em.merge(reference); // update date
                return reference;
            }

            reference = new FileReference();

            // store
            File fileInRepo = getNewPath(checksum);
            FileUtils.copyFile(resource, fileInRepo);

            ContentType contentType = ContentType.TEMP;

            fileInRepo.setExecutable(false);

            reference.setContentType(contentType);
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
