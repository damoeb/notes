package org.notes.core.services.internal;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.core.domain.StandardFileReference;
import org.notes.core.services.FileReferenceService;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class FileReferenceServiceImpl implements FileReferenceService {

    private static final Logger LOGGER = Logger.getLogger(FileReferenceServiceImpl.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @PostConstruct
    public void onInit() {
        File repository = getRepository();
        if (!repository.exists()) {
            repository.mkdirs();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getRepository() {
        return new File(getRepositoryPath());
    }

    /**
     * {@inheritDoc}
     */
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
            String message = String.format("Cannot run store. Reason: %s", t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public StandardFileReference getFileReference(Long fileId) throws NotesException {
        try {
            if (fileId == null || fileId <= 0) {
                throw new IllegalArgumentException(String.format("Invalid file id '%s'", fileId));
            }

            Query query = em.createNamedQuery(StandardFileReference.QUERY_BY_ID);
            query.setParameter("ID", fileId);

            List<StandardFileReference> fileReferences = query.getResultList();
            if (fileReferences.isEmpty()) {
                throw new IllegalArgumentException(String.format("No file with id '%s' found", fileId));
            }

            return fileReferences.get(0);

        } catch (Throwable t) {
            String message = String.format("Cannot run getFileReference, fileId=%s. Reason: %s", fileId, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        }
    }


    // -- Internal

    private String getRepositoryPath() {
        return System.getProperty("java.io.tmpdir") + File.separator + "notes";
    }

    private String getContentType(File file) throws IOException {
        return Files.probeContentType(Paths.get(file.toURI()));
    }

    private StandardFileReference find(String checksum, long size) {

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
    }

    private File getNewPath(String checksum) throws NoSuchAlgorithmException {

        // todo: ensure it does not exist
        return new File(getRepositoryPath() + File.separator + checksum + "." + System.currentTimeMillis() + ".dat");
    }

    private String getChecksum(InputStream stream) throws IOException {
        return DigestUtils.md5Hex(stream);
    }

}
