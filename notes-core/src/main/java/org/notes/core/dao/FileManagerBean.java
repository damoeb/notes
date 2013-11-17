package org.notes.core.dao;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.common.model.FileReference;
import org.notes.core.interfaces.FileManager;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class FileManagerBean implements FileManager {

    private static final Logger LOGGER = Logger.getLogger(FileManagerBean.class);

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
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public FileReference store(FileItem item) throws NotesException {

        try {

            if (item == null) {
                throw new IllegalArgumentException("item is null");
            }

            String checksum = getChecksum(item);

            FileReference reference = find(checksum, item.getSize());

            if (reference != null) {
                return reference;
            }

            reference = new FileReference();

            // store
            File fileInRepo = getNewPath(checksum);
            item.write(fileInRepo);

            String contentType = getContentType(fileInRepo);

            if (!validContentType(contentType)) {
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

    private boolean validContentType(String contentType) {
        // todo implement
        return true;
    }

    private String getContentType(File file) throws IOException {
        return Files.probeContentType(Paths.get(file.toURI()));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public FileReference find(String checksum, long size) throws NotesException {
        try {

            if (StringUtils.isBlank(checksum)) {
                throw new IllegalArgumentException("checksum is null");
            }

            if (size <= 0) {
                throw new IllegalArgumentException("invalid size " + size);
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

    private String getChecksum(FileItem item) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest((item.getName() + item.toString()).getBytes());
        BigInteger bigInt = new BigInteger(1, digest);
        String hashtext = bigInt.toString(16);
        // Now we need to zero pad it if you actually want the full 32 chars.
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }
}
