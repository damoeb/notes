package org.notes.core.dao;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.common.utils.TextUtils;
import org.notes.core.interfaces.*;
import org.notes.core.model.*;

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
public class TextDocumentManagerBean implements TextDocumentManager {

    private static final Logger LOGGER = Logger.getLogger(TextDocumentManagerBean.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private TextManager textManager;

    @Inject
    private FileManager fileManager;

    @Inject
    private FolderManager folderManager;

    @Inject
    private UserManager userManager;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TextDocument createDocument(TextDocument document) throws NotesException {

        try {

            if (document == null) {
                throw new NotesException("document is null");
            }

            document.setKind(Kind.TEXT);

            if (document.getFolderId() == null) {
                throw new NotesException("folderId is null");
            }

            Session session = em.unwrap(Session.class);

            //Folder folder = folderManager.getFolder(document.getFolderId());
            Folder folder = (Folder) session.load(Folder.class, document.getFolderId());
            if (folder == null) {
                throw new NotesException(String.format("folder with id %s is null", document.getFolderId()));
            }

            User user = userManager.getUser(1l); // todo userId

            //document.setFulltext(_getFulltext(document));
            document.setOutline(_getOutline(document));

            em.persist(document);
            em.flush();
            em.refresh(document);

            folder.getDocuments().add(document);
            folder.setDocumentCount(folder.getDocumentCount() + 1);
            em.merge(folder);

            while (folder.getParentId() != null) {
                Folder parent = (Folder) session.load(Folder.class, folder.getParentId());
                parent.getInheritedDocuments().add(document);
                folder = parent;
            }

            user.getDocuments().add(document);
            em.merge(user);

            return document;

        } catch (NotesException e) {
            throw e;
        } catch (Throwable t) {
            throw new NotesException("add document", t);
        }
    }

    private String _getOutline(TextDocument document) {
        return TextUtils.toOutline(document.getText());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TextDocument getDocument(long documentId) throws NotesException {
        try {
            Query query = em.createNamedQuery(Document.QUERY_BY_ID);
            query.setParameter("ID", documentId);
            return (TextDocument) query.getSingleResult();

        } catch (NoResultException t) {
            throw new NotesException("document '" + documentId + "' does not exist");
        } catch (Throwable t) {
            throw new NotesException("get document failed: " + t.getMessage(), t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TextDocument deleteDocument(TextDocument d) throws NotesException {
        try {
            Query query = em.createNamedQuery(Document.QUERY_MARK_DELETED);
            query.setParameter("ID", d.getId());
            query.setParameter("OWNER", 1l); // todo userId

            query.executeUpdate();

            return d;

        } catch (Throwable t) {
            throw new NotesException("delete document failed: " + t.getMessage(), t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TextDocument updateDocument(TextDocument newDoc) throws NotesException {
        try {

            if (newDoc == null) {
                throw new IllegalArgumentException("document is null");
            }

            TextDocument oldDoc = getDocument(newDoc.getId());

            // has document been moved?
            if (newDoc.getFolderId() != null && newDoc.getFolderId() != oldDoc.getFolderId()) {
                // todo implement
//                // test if new folder exists
//                folderManager.getFolder(newDoc.getFolderId());
//
//                // update folderId
//                oldDoc.setFolderId(newDoc.getFolderId());
//                em.merge(oldDoc);
            }

            oldDoc.setTitle(newDoc.getTitle());
            oldDoc.setText(newDoc.getText());

            oldDoc.setOutline(_getOutline(oldDoc));

            em.merge(oldDoc);
            em.flush();
            em.refresh(oldDoc);

            return oldDoc;

        } catch (NotesException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesException("update document failed: " + t.getMessage(), t);
        }
    }

}
