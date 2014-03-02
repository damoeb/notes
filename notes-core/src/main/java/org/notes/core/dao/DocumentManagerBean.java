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
import org.notes.common.model.Tag;
import org.notes.common.model.Trigger;
import org.notes.core.interfaces.*;
import org.notes.core.model.*;
import org.notes.search.scheduler.ExtractionScheduler;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.net.MalformedURLException;
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
    private DatabaseManager databaseManager;

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

            LOGGER.info("create text-document");

            if (document == null) {
                throw new NotesException("document is null");
            }

            if (inFolder == null || inFolder.getId() == 0) {
                inFolder = databaseManager.getDatabaseOfUser().getDefaultFolder();
            } else {
                if (!em.contains(inFolder)) {
                    inFolder = folderManager.getFolder(inFolder.getId());
                }
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

        Session session = em.unwrap(Session.class);

        Folder proxy = (Folder) session.load(Folder.class, inFolder.getId());
        if (proxy == null) {
            throw new NotesException(String.format("folder with id %s is null", document.getFolderId()));
        }

        proxy.getDocuments().add(document);
        proxy.setDocumentCount(proxy.getDocumentCount() + 1);
        em.merge(user);
        em.merge(proxy);
        em.flush();

        em.refresh(document);

        document.setUniqueHash(getUniqueHash(document));
        em.merge(document);

        return document;
    }

    private String getUniqueHash(BasicDocument document) {
        return Long.toHexString(document.getId() * 1000000 + System.nanoTime() % 1000000);
    }


    @Override
    public List<BasicDocument> getDocumentsInFolder(Long folderId) throws NotesException {
        try {
            if (folderId == null || folderId <= 0) {
                throw new NotesException(String.format("Invalid folder id '%s'", folderId));
            }

            Query query = em.createNamedQuery(BasicDocument.QUERY_IN_FOLDER);
            query.setParameter("ID", folderId);

            return (List<BasicDocument>) query.getResultList();

        } catch (NotesException e) {
            throw e;
        } catch (Throwable t) {
            throw new NotesException("get documents of " + folderId, t);
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
            LOGGER.info("delete document");

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
    public BasicDocument updateBasicDocument(BasicDocument ref) throws NotesException {
        try {

            LOGGER.info("update document");

            if (ref == null) {
                throw new IllegalArgumentException("document is null");
            }

            BasicDocument document = _get(ref.getId());

            if (!equals(ref, document)) {

                copyAttributes(ref, document);

                document.setTrigger(Trigger.INDEX);

                em.merge(document);
                em.flush();
                em.refresh(document);
            }

            Hibernate.initialize(document.getTags());

            return document;

        } catch (NotesException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesException("update document failed: " + t.getMessage(), t);
        }
    }

    private void copyAttributes(BasicDocument source, BasicDocument target) throws NotesException {
        target.setTags(resolveTags(target.getTags(), source.getTags()));
        target.setStar(source.isStar());
        target.setTitle(source.getTitle());
    }

    private boolean equals(BasicDocument a, BasicDocument b) {
        boolean similarTitle = StringUtils.equals(b.getTitle(), a.getTitle());
        boolean similarStarState = b.isStar() == a.isStar();

        return similarTitle && equalsTags(b.getTags(), a.getTags()) && similarStarState;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BasicDocument updateTextDocument(TextDocument ref) throws NotesException {
        try {

            LOGGER.info("update text-document");

            if (ref == null) {
                throw new IllegalArgumentException("document is null");
            }

            TextDocument document = (TextDocument) _get(ref.getId());
            boolean similarText = StringUtils.equals(ref.getText(), document.getText());

            if (!equals(ref, document) || !similarText) {

                copyAttributes(ref, document);

                document.setText(ref.getText());

                document.setTrigger(Trigger.INDEX);

                em.merge(document);
                em.flush();
                em.refresh(document);
            }


            Hibernate.initialize(document.getTags());

            return document;

        } catch (NotesException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesException("update text-document failed: " + t.getMessage(), t);
        }
    }

    private boolean equalsTags(Set<? extends Tag> a, Set<? extends Tag> b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null) {
            return false;
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

        Map<String, DefaultTag> cache = new HashMap<>();
        for (Tag c : cached) {
            cache.put(c.getName(), (DefaultTag) c);
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
            LOGGER.info("upload document");

            FileReference reference = null;
            String title = null;

            Long folderId = NumberUtils.createLong(_getFieldValue("folderId", items));

            Folder inFolder;
            if (folderId == null || folderId == 0) {
                LOGGER.info("in default folder " + folderId);
                inFolder = databaseManager.getDatabaseOfUser().getDefaultFolder();
            } else {
                LOGGER.info("in folder " + folderId);
                inFolder = folderManager.getFolder(folderId);
            }

            for (FileItem item : items) {

                if (!item.isFormField()) {

                    reference = fileReferenceManager.store(item);
                    title = item.getName();
                    break;
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

            LOGGER.info("bookmark document");

            if (ref == null) {
                throw new NotesException("bookmark is invalid");
            }

            validateUrl(ref.getUrl());

            if (inFolder == null || inFolder.getId() == 0) {
                inFolder = databaseManager.getDatabaseOfUser().getDefaultFolder();
            } else {
                if (!em.contains(inFolder)) {
                    inFolder = folderManager.getFolder(inFolder.getId());
                }
            }

            BookmarkDocument document = new BookmarkDocument();
            document.setUrl(ref.getUrl());
            document.setTitle(ref.getUrl());

            document.setTrigger(Trigger.HARVEST);

            document = (BookmarkDocument) _createDocument(document, inFolder);

//            extract text
//            to pdf -> to tmp storage
//            annotate/crop on client

            Hibernate.initialize(document.getTags());

            return document;

        } catch (NotesException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesException("bookmark failed: " + t.getMessage(), t);
        }
    }

    private void validateUrl(String url) throws NotesException {

        if (StringUtils.isBlank(url)) {
            throw new NotesException("url is empty");
        }

        try {
            new URL(url);
        } catch (MalformedURLException e) {
            throw new NotesException("url is invalid");
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
