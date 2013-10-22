package org.notes.core.dao;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.core.interfaces.FileManager;
import org.notes.core.model.FileReference;
import org.notes.core.model.Note;
import org.notes.core.request.NotesRequestException;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
@Deprecated
public class FileManagerBean implements FileManager {

    private static final Logger LOGGER = Logger.getLogger(FileManagerBean.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @PostConstruct
    public void onInit() {
        File repository = getRepository();
        if(!repository.exists()) {
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
    public File getTempRepository() {
        return new File(System.getProperty("java.io.tmpdir"));
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public RepositoryFile storeInRepository(FileItem item) {

        try {

            if (item == null) {
                throw new IllegalArgumentException("item is null");
            }

            File fileInRepo = getNewPath(item);

            // store
            item.write(fileInRepo);
            fileInRepo.setExecutable(false);

            RepositoryFile repositoryFile = new RepositoryFile();
            repositoryFile.setContentType(getContentType(fileInRepo));
            repositoryFile.setSize(item.getSize());
            repositoryFile.setPath(fileInRepo.getAbsolutePath());

            return repositoryFile;

        } catch (NotesRequestException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesRequestException("upload file", t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public FileReference find(String checksum, long size) throws NotesException {
        try {

            if (StringUtils.isBlank(checksum)) {
                throw new IllegalArgumentException("checksum is null");
            }

            if (size <= 0) {
                throw new IllegalArgumentException("invalid size "+size);
            }

            Query query = em.createNamedQuery(FileReference.QUERY_BY_CHECKSUM);
            query.setParameter("CHECKSUM", checksum);
            query.setParameter("FILESIZE", size);

            List<FileReference> list = (List<FileReference>) query.getResultList();
            if(list.isEmpty()) {
                return null;
            }

            return list.get(0);

        } catch (Throwable t) {
            throw new NotesException(t.getMessage());
        }
    }

    private String getContentType(File fileInRepo) throws IOException, InterruptedException {
        /*
        Process process = null;
        String contentType = null;
        try {
            process = new ProcessBuilder("mimetype", "-i", "--magic-only", "--output-format", "%m", fileInRepo.getAbsolutePath()).start();
            process.waitFor();
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

            contentType = StringUtils.trim(br.readLine());

            if(process.exitValue()!=0) {
                LOGGER.error("mimetype command exists with "+process.exitValue());
            }

        } finally {
            if(process!=null) {
                process.getInputStream().close();
                process.getErrorStream().close();
                process.getOutputStream().close();
                process.destroy();
            }

        }
        return contentType;
        */
        return Files.probeContentType(Paths.get(fileInRepo.toURI()));
    }

    private File getNewPath(FileItem item) throws NoSuchAlgorithmException {

        // create filename

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest((item.getName() + item.toString()).getBytes());
        BigInteger bigInt = new BigInteger(1,digest);
        String hashtext = bigInt.toString(16);
        // Now we need to zero pad it if you actually want the full 32 chars.
        while(hashtext.length() < 32 ){
            hashtext = "0"+hashtext;
        }

        // todo: ensure it does not exist
        return new File(getRepositoryPath() + File.separator + hashtext + "." + System.currentTimeMillis() + ".dat");
    }
}
