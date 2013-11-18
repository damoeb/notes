package org.notes.core.dao;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.common.model.Event;
import org.notes.common.model.FileReference;
import org.notes.common.model.Kind;
import org.notes.common.utils.TextUtils;
import org.notes.core.interfaces.FileManager;
import org.notes.core.interfaces.FolderManager;
import org.notes.core.interfaces.TextDocumentManager;
import org.notes.core.interfaces.UserManager;
import org.notes.core.model.*;
import org.notes.search.interfaces.SearchManager;
import org.notes.search.interfaces.TextManager;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

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
    private FolderManager folderManager;

    @Inject
    private FileManager fileManager;

    @Inject
    private UserManager userManager;

    @Inject
    private SearchManager searchManager;

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

            Long folderId = document.getFolderId();

            //document.setFulltext(_getFulltext(document));
            document.setOutline(_getOutline(document));
            document.setProgress(_getProgress(document));

            document = (TextDocument) _createDocument(document, folderId);

            return document;

        } catch (NotesException e) {
            throw e;
        } catch (Throwable t) {
            throw new NotesException("add document", t);
        }
    }

    private Document _createDocument(Document document, Long folderId) throws NotesException {

        em.persist(document);

        User user = userManager.getUser(1l); // todo userId
        user.getDocuments().add(document);
        em.merge(user);

        _addToParentFolders(document, folderId);


        em.flush();

        em.refresh(document);

        // -- Postprocesing --
        //searchManager.index(document);

        return document;
    }

    private void _addToParentFolders(Document document, Long folderId) throws NotesException {

        Session session = em.unwrap(Session.class);

        //Folder folder = folderManager.getFolder(document.getFolderId());
        Folder folder = (Folder) session.load(Folder.class, folderId);
        if (folder == null) {
            throw new NotesException(String.format("folder with id %s is null", document.getFolderId()));
        }

        folder.getDocuments().add(document);
        folder.setDocumentCount(folder.getDocumentCount() + 1);
        em.merge(folder);

        while (folder.getParentId() != null) {
            Folder parent = (Folder) session.load(Folder.class, folder.getParentId());
            parent.getInheritedDocuments().add(document);
            folder = parent;
        }
    }

    private String _getOutline(TextDocument document) {
        return TextUtils.toOutline(document.getText());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Document getDocument(long documentId) throws NotesException {
        try {
            Query query = em.createNamedQuery(Document.QUERY_WITH_REMINDER);
            query.setParameter("ID", documentId);
            return (Document) query.getSingleResult();

        } catch (NoResultException t) {
            throw new NotesException("document '" + documentId + "' does not exist");
        } catch (Throwable t) {
            throw new NotesException("get document failed: " + t.getMessage(), t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Document deleteDocument(Document d) throws NotesException {
        try {
            Query query = em.createNamedQuery(Document.DELETE_DOCUMENT);
            query.setParameter("ID", d.getId());
            query.setParameter("OWNER", 1l); // todo userId

            query.executeUpdate();

            // -- Postprocesing --
            //searchManager.delete(d);

            return d;

        } catch (Throwable t) {
            throw new NotesException("delete document failed: " + t.getMessage(), t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Document updateDocument(Document newDoc) throws NotesException {
        try {

            if (newDoc == null) {
                throw new IllegalArgumentException("document is null");
            }

            Document oldDoc = _get(newDoc.getId());

            // decide whether move or update
            if (Event.MOVE.equals(newDoc.getEvent())) {

                Query query;

                query = em.createNativeQuery("UPDATE Folder f SET f.documentCount = f.documentCount - 1 WHERE f.id = :FOLDER_ID");
                query.setParameter("FOLDER_ID", oldDoc.getFolderId());
                query.executeUpdate();

                query = em.createNativeQuery("DELETE FROM folder2document WHERE document_id = :DOC_ID");
                query.setParameter("DOC_ID", oldDoc.getId());
                query.executeUpdate();

                em.flush();

                _addToParentFolders(oldDoc, newDoc.getFolderId());
            }

            if (Event.UPDATE.equals(newDoc.getEvent())) {

                TextDocument tdoc = (TextDocument) oldDoc;
                TextDocument ndoc = (TextDocument) newDoc;

                tdoc.setTitle(ndoc.getTitle());
                tdoc.setText(ndoc.getText());

                oldDoc.setOutline(_getOutline(tdoc));
                oldDoc.setProgress(_getProgress(newDoc));

                Reminder reminder = newDoc.getReminder();
                if (oldDoc.getReminderId() == null) {
                    oldDoc.setReminder(reminder);
                } else {
                    if (reminder == null) {
                        oldDoc.setReminder(null);

                    } else {
                        reminder.setId(oldDoc.getReminderId());
                        em.merge(reminder);
                    }
                }

                em.merge(oldDoc);
                em.flush();
                em.refresh(oldDoc);

            }

            // -- Postprocesing --
            //searchManager.index(oldDoc);

            return oldDoc;

        } catch (NotesException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesException("update document failed: " + t.getMessage(), t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FileDocument uploadDocument(List<FileItem> items) throws NotesException {
        try {

            FileReference reference = null;
            String title = null;

            Long folderId = NumberUtils.createLong(_getFieldValue("folderId", items));

            for (FileItem item : items) {

                if (!item.isFormField()) {

                    try {
                        reference = fileManager.store(item);
                        title = item.getName();
                        break;

                    } catch (NotesException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (reference == null || folderId == null) {
                throw new IllegalArgumentException("No valid files found");
            }

            FileDocument document = new FileDocument();
            document.setKind(Kind.PDF);
            document.setTitle(title);
            document.setFileReference(reference);
            document.setOutline(_getFileOutline(reference));

            document = (FileDocument) _createDocument(document, folderId);

            // -- Postprocesing --

            //searchManager.index(document);

            return document;

        } catch (Throwable t) {
            throw new NotesException("upload document failed: " + t.getMessage(), t);
        }
    }

    private String _getFileOutline(FileReference reference) {
        return TextUtils.toOutline(reference.getSize() + " bytes", reference.getFullText());
    }

    private String _getFieldValue(String fieldName, List<FileItem> items) throws NotesException {
        for (FileItem item : items) {

            if (item.isFormField()) {

                String someFieldName = item.getFieldName();
                if (StringUtils.equalsIgnoreCase(someFieldName, fieldName)) {
                    return item.getString();
                }
            }
        }

        return null;
    }

    private Integer _getProgress(Document document) {
        Integer progress = document.getProgress();
        if (progress == null || progress <= 0 || progress > 100) {
            return null;
        }
        return progress;
    }

    private Document _get(long documentId) throws NotesException {
        Query query = em.createNamedQuery(Document.QUERY_BY_ID);
        query.setParameter("ID", documentId);
        return (Document) query.getSingleResult();
    }
}
