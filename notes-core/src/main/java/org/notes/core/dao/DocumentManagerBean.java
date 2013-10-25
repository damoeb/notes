package org.notes.core.dao;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.notes.common.configuration.Configuration;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.core.interfaces.FileManager;
import org.notes.core.interfaces.DocumentManager;
import org.notes.core.interfaces.TextManager;
import org.notes.core.model.Attachment;
import org.notes.core.model.Document;
import org.notes.core.model.FileReference;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;

//@LocalBean
@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class DocumentManagerBean implements DocumentManager {

    private static final Logger LOGGER = Logger.getLogger(DocumentManagerBean.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private TextManager textManager;

    @Inject
    private FileManager fileManager;

    private int maxResults;

    @PostConstruct
    public void onInit() {

        maxResults = Configuration.getIntValue("query.max.results", 1000);

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Document getById(long documentId) throws NotesException {
        try {
            Query query = em.createNamedQuery(Document.QUERY_BY_ID);
            query.setParameter("ID", documentId);
            Document note = (Document) query.getSingleResult();

            return note;

        } catch (NoResultException t) {
            throw new NotesException("note '"+ documentId +"' does not exist");
        } catch (Throwable t) {
            throw new NotesException("get note by id", t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Document getByIdWithRefs(long documentId) throws NotesException {
        try {
            Document note = getById(documentId);
            Hibernate.initialize(note.getAttachments());

            return note;

        } catch (NotesException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesException("get note by id", t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Document addDocument(Document document) throws NotesException {

        try {

            if (document == null) {
                throw new IllegalArgumentException("note is null");
            }

            // todo validate

            em.persist(document);
            em.flush();
            em.refresh(document);

            return document;

        } catch (Throwable t) {
            throw new NotesException("add note", t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Document updateNote(long documentId, Document document) throws NotesException {

        try {

            if (document == null) {
                throw new IllegalArgumentException("note is null");
            }

            Document oldNote = getById(documentId);

            oldNote.setTitle(document.getTitle());
            oldNote.setText(document.getText());

            boolean urlChanged = StringUtils.equals(oldNote.getUrl(), document.getUrl());
            if(urlChanged) {

            }

            oldNote.setUrl(document.getUrl());
            oldNote.onPersist();

            em.merge(oldNote);
            em.flush();
            em.refresh(oldNote);

            return oldNote;

        } catch (NotesException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesException("update note", t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeNote(long documentId) throws NotesException {
        try {

            Document note = getById(documentId);
            /*
            Hibernate.initialize(note.getAttachments());

            if(note.getAttachments()!=null) {
                for(FileReference attachment : note.getAttachments()) {
                    try {
                        new File(attachment.getReference()).delete();
                    } catch (Exception f) {
                        LOGGER.fatal(String.format("File %s does not exist. (note %s)", attachment.getReference(), noteId));
                    }

                    em.remove(attachment);
                }
            }

            note.getAttachments().clear();
            */
            em.remove(note);

        } catch (NotesException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesException("remove note", t);
        }
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeAttachmentFromNote(long attachmentId, long documentId) throws NotesException {
        try {
            Document note = getById(documentId);
            Hibernate.initialize(note.getAttachments());

            Attachment attachment = null;
            if(note.getAttachments()!=null) {
                for(Attachment a : note.getAttachments()) {
                    if(a.getId()==attachmentId) {
                        attachment = a;
                        break;
                    }
                }
            }

            if(attachment==null) {
                throw new NotesException(String.format("Attachment with id %s does not exist", attachment));
            }

            /*
            try {
                new File(attachment.getReference()).delete();
            } catch (Exception f) {
                LOGGER.fatal(String.format("File %s does not exist. (note %s)", attachment.getReference(), noteId));
            }
            */

            note.getAttachments().remove(attachment);
            em.merge(note);
            em.remove(attachment);

        } catch (NotesException t) {
            throw t;
        } catch (RuntimeException t) {
            throw new NotesException("remove Attachment", t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Attachment addAttachmentToNote(String fileName, RepositoryFile repositoryFile, Document document) throws NotesException {

        try {

            if (StringUtils.isBlank(fileName)) {
                throw new IllegalArgumentException("fileName is null");
            }
            if (repositoryFile == null) {
                throw new IllegalArgumentException("repositoryFile is null");
            }
            if (document == null) {
                throw new IllegalArgumentException("note is null");
            }

            String checksum = _getChecksum(repositoryFile);
            long size = repositoryFile.getSize();

            FileReference reference = fileManager.find(checksum, size);
            // create new ref
            if(reference == null) {
                reference = new FileReference();
                reference.setReference(repositoryFile.getPath());
                reference.setSize(size);
                reference.setContentType(repositoryFile.getContentType());
                reference.setChecksum(checksum);

                em.persist(reference);
                em.flush();
                em.refresh(reference);

                // trigger text extraction
                textManager.extractAsync(reference);
            }

            // create attachment
            Attachment attachment = new Attachment();
            attachment.setName(fileName);
            attachment.setSize(size); // todo redundant!
            attachment.setContentType(repositoryFile.getContentType()); // todo redundant!
            attachment.setFileReference(reference);

            em.persist(attachment);
            em.flush();
            em.refresh(attachment);

            if(document.getAttachments().contains(attachment)) {
                throw new IllegalArgumentException("attachment already part of note");
            }

            document.getAttachments().add(attachment);
            document.setHasAttachments(true);
            document.onPersist();
            em.merge(document);

            return attachment;

        } catch (Throwable t) {
            throw new NotesException("upload file", t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Attachment renameAttachment(long attachmentId, String newName) throws NotesException {
        try {

            if(StringUtils.isBlank(newName)) {
                throw new IllegalArgumentException("name is empty");
            }

            Query query = em.createNamedQuery(Attachment.QUERY_BY_ID);
            query.setParameter("ID", attachmentId);

            Attachment attachment = (Attachment) query.getSingleResult();
            attachment.setName(newName);
            em.merge(attachment);
            em.flush();
            em.refresh(attachment);

            return attachment;

        } catch (Throwable t) {
            throw new NotesException("rename Attachment; "+t.getMessage(), t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Attachment getAttachmentWithFile(long attachmentId) throws NotesException {
        try {

            Query query = em.createNamedQuery(Attachment.QUERY_BY_ID);
            query.setParameter("ID", attachmentId);

            Attachment attachment = (Attachment) query.getSingleResult();
            Hibernate.initialize(attachment.getFileReference());

            return attachment;

        } catch (Throwable t) {
            throw new NotesException("rename Attachment; "+t.getMessage(), t);
        }
    }

    private String _getChecksum(RepositoryFile file) throws NotesException {

        InputStream is = null;
        try {
            is = new FileInputStream(new File(file.getPath()));

            MessageDigest digest = MessageDigest.getInstance("MD5");

            byte[] buffer = new byte[8192];
            int read = 0;
            while( (read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            return output;
        }
        catch(Exception e) {
            throw new NotesException("Unable to process file for MD5", e);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<Document> getList(int firstResult, int maxResults) throws NotesException {
        try {
            _verifyLimits(firstResult, maxResults);

            Query query = em.createNamedQuery(Document.QUERY_ALL);
            query.setFirstResult(firstResult);
            query.setMaxResults(maxResults);

            @SuppressWarnings("unchecked")
            List<Document> list = query.getResultList();
            for (Document note : list) {
                em.detach(note);
                note.setAttachments(null);
            }
            return list;

        } catch (Throwable t) {
            throw new NotesException("getList failed: " + t.getMessage(), t);
        }
    }

    private void _verifyLimits(int firstResult, int customMaxResults) {
        if (customMaxResults == 0) {
            throw new IllegalArgumentException("maxResults is 0");
        }
        if (customMaxResults > maxResults) {
            throw new IllegalArgumentException("maxResults exceeds upper limit " + maxResults);
        }
        if (firstResult < 0) {
            throw new IllegalArgumentException("firstResult < 0");
        }
    }

}
