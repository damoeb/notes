package org.notes.core.dao;

import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.common.model.Kind;
import org.notes.common.utils.TextUtils;
import org.notes.core.interfaces.FileDocumentManager;
import org.notes.core.interfaces.UserManager;
import org.notes.core.model.Document;
import org.notes.core.model.FileDocument;
import org.notes.search.interfaces.SearchManager;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

//@LocalBean
@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class FileDocumentManagerBean implements FileDocumentManager {

    private static final Logger LOGGER = Logger.getLogger(FileDocumentManagerBean.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private UserManager userManager;

    @Inject
    private SearchManager searchManager;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FileDocument createDocument(FileDocument document) throws NotesException {

        try {

            if (document == null) {
                throw new NotesException("document is null");
            }

            document.setKind(Kind.PDF);

            if (document.getFolderId() == null) {
                throw new NotesException("folderId is null");
            }

            return document;

        } catch (NotesException e) {
            throw e;
        } catch (Throwable t) {
            throw new NotesException("add document", t);
        }
    }

    private String _getOutline(FileDocument document) {
        return TextUtils.toOutline(document.getAttachment().getFileReference().getFullText());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FileDocument getDocument(long documentId) throws NotesException {
        try {
            Query query = em.createNamedQuery(Document.QUERY_WITH_REMINDER);
            query.setParameter("ID", documentId);
            return (FileDocument) query.getSingleResult();

        } catch (NoResultException t) {
            throw new NotesException("document '" + documentId + "' does not exist");
        } catch (Throwable t) {
            throw new NotesException("get document failed: " + t.getMessage(), t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FileDocument deleteDocument(FileDocument d) throws NotesException {
        try {
            return d;

        } catch (Throwable t) {
            throw new NotesException("delete document failed: " + t.getMessage(), t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FileDocument updateDocument(FileDocument newDoc) throws NotesException {
        try {

            if (newDoc == null) {
                throw new IllegalArgumentException("document is null");
            }

            return newDoc;

        } catch (Throwable t) {
            throw new NotesException("update document failed: " + t.getMessage(), t);
        }
    }
}
