package org.notes.core.dao;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.common.model.FileReference;
import org.notes.common.model.Trigger;
import org.notes.core.interfaces.*;
import org.notes.core.model.*;
import org.notes.text.scheduler.ExtractionScheduler;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.net.URL;
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
    private FolderManager folderManager;

    @Inject
    private FileReferenceManager fileReferenceManager;

    @Inject
    private ExtractionScheduler extractionScheduler;

    @Inject
    private UserManager userManager;

    @Inject
    private TagManager tagManager;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TextDocument createDocument(TextDocument document, Folder inFolder) throws NotesException {

        try {

            if (document == null) {
                throw new NotesException("document is null");
            }

            if (inFolder == null) {
                throw new NotesException("folder is null");
            }

            if (!em.contains(inFolder)) {
                inFolder = folderManager.getFolder(inFolder.getId());
            }

            document.setTrigger(Trigger.INDEX);

            BasicDocument basicDocument = _createDocument(document, inFolder);
            Hibernate.initialize(basicDocument.getTags());
            return (TextDocument) basicDocument;

        } catch (NotesException e) {
            throw e;
        } catch (Throwable t) {
            throw new NotesException("add document", t);
        }
    }

    private BasicDocument _createDocument(BasicDocument document, Folder inFolder) throws NotesException {

        document.validate();

        em.persist(document);

        User user = userManager.getUser(inFolder.getOwner());
        user.getDocuments().add(document);
        em.merge(user);

        _addToParentFolders(document, inFolder);

        em.flush();

        em.refresh(document);

        return document;
    }

    private void _addToParentFolders(BasicDocument document, Folder folder) throws NotesException {

        Session session = em.unwrap(Session.class);

        Folder proxy = (Folder) session.load(Folder.class, folder.getId());
        if (proxy == null) {
            throw new NotesException(String.format("folder with id %s is null", document.getFolderId()));
        }

        proxy.getDocuments().add(document);
        proxy.setDocumentCount(proxy.getDocumentCount() + 1);
        em.merge(proxy);

        while (proxy.getParentId() != null) {
            Folder parent = (Folder) session.load(Folder.class, proxy.getParentId());
            parent.getInheritedDocuments().add(document);
            parent.setDocumentCount(parent.getDocumentCount() + 1);
            proxy = parent;
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BasicDocument getDocument(long documentId) throws NotesException {
        try {

            Query query = em.createNamedQuery(BasicDocument.QUERY_BY_ID);
            query.setParameter("ID", documentId);
            BasicDocument document = (BasicDocument) query.getSingleResult();
            Hibernate.initialize(document.getTags());
            return document;

        } catch (NoResultException t) {
            throw new NotesException("document '" + documentId + "' does not exist");
        } catch (Throwable t) {
            throw new NotesException("get document failed: " + t.getMessage(), t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BasicDocument deleteDocument(long documentId) throws NotesException {
        try {
            BasicDocument document = _get(documentId);
            document.setDeleted(true);
            document.setTrigger(Trigger.DELETE);

            Session session = em.unwrap(Session.class);

            // update document count
            Folder proxy = (Folder) session.load(Folder.class, document.getFolderId());
            proxy.setDocumentCount(proxy.getDocumentCount() - 1);
            em.merge(proxy);

            while (proxy.getParentId() != null) {
                Folder parent = (Folder) session.load(Folder.class, proxy.getParentId());
                parent.setDocumentCount(parent.getDocumentCount() - 1);
                proxy = parent;
            }

            em.merge(document);

            Hibernate.initialize(document.getTags());

            return document;

        } catch (Throwable t) {
            throw new NotesException("delete document failed: " + t.getMessage(), t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BasicDocument updateDocument(BasicDocument ref) throws NotesException {
        try {

            if (ref == null) {
                throw new IllegalArgumentException("document is null");
            }

            BasicDocument document = _get(ref.getId());

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

                // todo fix
                // _addToParentFolders(document, ref.getFolderId());
            }

            if (Event.UPDATE.equals(ref.getEvent())) {

                TextDocument txtDoc = (TextDocument) document;
                TextDocument txtRef = (TextDocument) ref;

                boolean similarTitle = StringUtils.equals(txtDoc.getTitle(), txtRef.getTitle());
                boolean similarText = StringUtils.equals(txtDoc.getText(), txtRef.getText());

                Set<Tag> tagsDoc = txtDoc.getTags();
                Set<Tag> tagsRef = txtRef.getTags();

                boolean hasChanged = !(similarTitle && similarText && equalsTags(tagsDoc, tagsRef));

                if (hasChanged) {
                    // todo calc diff
                    // Patch patch = DiffUtils.diff(Arrays.asList(txtDoc.getTitle(), txtDoc.getText()), Arrays.asList(txtRef.getTitle(), txtRef.getText()));

                    txtDoc.setTags(resolveTags(tagsDoc, tagsRef));

                    // update
                    txtDoc.setTitle(txtRef.getTitle());
                    txtDoc.setText(txtRef.getText());

                    document.setTrigger(Trigger.INDEX);

                    em.merge(document);
                    em.flush();
                    em.refresh(document);
                }

            }

            Hibernate.initialize(document.getTags());

            return document;

        } catch (NotesException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesException("update document failed: " + t.getMessage(), t);
        }
    }

    private boolean equalsTags(Set<Tag> a, Set<Tag> b) {
        if (a == null && b == null) {
            return true;
        }
        if (a.isEmpty() && b.isEmpty()) {
            return true;
        }
        if (a.size() != b.size()) {
            return false;
        }

        for (Tag t : a) {
            if (!b.contains(t)) {
                return false;
            }
        }

        return true;
    }

    private Set<Tag> resolveTags(Set<Tag> cached, Set<Tag> tags) throws NotesException {
        Set<Tag> resolved = new HashSet<>(tags.size());

        Map<String, Tag> cache = new HashMap<>();
        for (Tag c : cached) {
            cache.put(c.getName(), c);
        }

        for (Tag t : tags) {
            if (cache.containsKey(t.getName())) {
                resolved.add(cache.get(t.getName()));
            } else {
                resolved.add(tagManager.findOrCreate(t.getName()));
            }
        }

        return resolved;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PdfDocument uploadDocument(List<FileItem> items) throws NotesException {
        try {

            FileReference reference = null;
            String title = null;

            Long folderId = NumberUtils.createLong(_getFieldValue("folderId", items));

            Folder inFolder = folderManager.getFolder(folderId);

            for (FileItem item : items) {

                if (!item.isFormField()) {

                    try {
                        reference = fileReferenceManager.store(item);
                        title = item.getName();
                        break;

                    } catch (NotesException e) {
                        e.printStackTrace();
                    }
                }
            }

            // todo uploaded file may be zip to create structure from
            // reference.getContentType()

            if (reference == null) {
                throw new IllegalArgumentException("No valid files found");
            }

            PdfDocument document = new PdfDocument();
            document.setTitle(title);
            document.setFileReference(reference);
            document.setTrigger(Trigger.EXTRACT_PDF);

            return (PdfDocument) _createDocument(document, inFolder);

        } catch (Throwable t) {
            throw new NotesException("upload document failed: " + t.getMessage(), t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BookmarkDocument bookmark(BookmarkDocument ref, Folder inFolder) throws NotesException {

        try {

            if (ref == null) {
                throw new NotesException("bookmark is invalid");
            }

            if (StringUtils.isBlank(ref.getUrl())) {
                throw new NotesException("url is empty");
            }
            new URL(ref.getUrl());


            if (ref == null) {
                throw new NotesException("bookmark is invalid");
            }

            if (inFolder == null) {
                throw new NotesException("folder is null");
            }

            if (!em.contains(inFolder)) {
                inFolder = folderManager.getFolder(inFolder.getId());
            }

            BookmarkDocument document = new BookmarkDocument();
            document.setUrl(ref.getUrl());
            document.setTitle(ref.getUrl());

            document.setTrigger(Trigger.HARVEST);

            document = (BookmarkDocument) _createDocument(document, inFolder);

//            extract text
//            to pdf -> to tmp storage
//            annotate/crop on client

            return document;

        } catch (NotesException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesException("bookmark failed: " + t.getMessage(), t);
        }
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

    private BasicDocument _get(long documentId) throws NotesException {
        Query query = em.createNamedQuery(BasicDocument.QUERY_BY_ID);
        query.setParameter("ID", documentId);
        return (BasicDocument) query.getSingleResult();
    }
}
