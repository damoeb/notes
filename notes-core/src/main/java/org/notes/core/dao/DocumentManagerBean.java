package org.notes.core.dao;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.common.model.*;
import org.notes.common.utils.TextUtils;
import org.notes.core.interfaces.DocumentManager;
import org.notes.core.interfaces.FileManager;
import org.notes.core.interfaces.FolderManager;
import org.notes.core.interfaces.UserManager;
import org.notes.core.model.Folder;
import org.notes.core.model.PdfDocument;
import org.notes.core.model.TextDocument;
import org.notes.core.model.User;

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
public class DocumentManagerBean implements DocumentManager {

    private static final Logger LOGGER = Logger.getLogger(DocumentManagerBean.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private FolderManager folderManager;

    @Inject
    private FileManager fileManager;

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

            Long folderId = document.getFolderId();

            document.setOutline(_getOutline(document));
            document.setProgress(_getProgress(document));
            document.setTrigger(Trigger.INDEX);

            return (TextDocument) _createDocument(document, folderId);

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
    public Document deleteDocument(Document ref) throws NotesException {
        try {
            Document document = _get(ref.getId());
            document.setDeleted(true);
            document.setTrigger(Trigger.DELETE);
            em.merge(document);

            return ref;

        } catch (Throwable t) {
            throw new NotesException("delete document failed: " + t.getMessage(), t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Document updateDocument(Document ref) throws NotesException {
        try {

            if (ref == null) {
                throw new IllegalArgumentException("document is null");
            }

            Document document = _get(ref.getId());

            // decide whether move or update
            if (Event.MOVE.equals(ref.getEvent())) {

                Query query;

                query = em.createNativeQuery("UPDATE Folder f SET f.documentCount = f.documentCount - 1 WHERE f.id = :FOLDER_ID");
                query.setParameter("FOLDER_ID", document.getFolderId());
                query.executeUpdate();

                query = em.createNativeQuery("DELETE FROM folder2document WHERE document_id = :DOC_ID");
                query.setParameter("DOC_ID", document.getId());
                query.executeUpdate();

                em.flush();

                _addToParentFolders(document, ref.getFolderId());
            }

            if (Event.UPDATE.equals(ref.getEvent())) {

                TextDocument tdoc = (TextDocument) document;
                TextDocument ndoc = (TextDocument) ref;

                tdoc.setTitle(ndoc.getTitle());
                tdoc.setText(ndoc.getText());

                document.setOutline(_getOutline(tdoc));
                document.setProgress(_getProgress(ref));

                Reminder reminder = ref.getReminder();
                if (document.getReminderId() == null) {
                    document.setReminder(reminder);
                } else {
                    if (reminder == null) {
                        document.setReminder(null);

                    } else {
                        reminder.setId(document.getReminderId());
                        em.merge(reminder);
                    }
                }

                document.setTrigger(Trigger.INDEX);

                em.merge(document);
                em.flush();
                em.refresh(document);

            }

            return document;

        } catch (NotesException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesException("update document failed: " + t.getMessage(), t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PdfDocument uploadDocument(List<FileItem> items) throws NotesException {
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

            // todo uploaded file may be zip to create structure from
            // reference.getContentType()

            if (reference == null || folderId == null) {
                throw new IllegalArgumentException("No valid files found");
            }

            PdfDocument document = new PdfDocument();
            document.setKind(Kind.PDF);
            document.setTitle(title);
            document.setFileReference(reference);
            document.setOutline(_getFileOutline(reference));

            document.setTrigger(Trigger.EXTRACT);

            return (PdfDocument) _createDocument(document, folderId);

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