package org.notes.core.services.internal;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.domain.FileReference;
import org.notes.common.domain.Folder;
import org.notes.common.domain.Tag;
import org.notes.common.exceptions.NotesException;
import org.notes.common.services.FolderService;
import org.notes.common.utils.TextUtils;
import org.notes.core.domain.*;
import org.notes.core.services.*;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.*;
import javax.jms.Queue;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

//@LocalBean
@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class DocumentServiceImpl implements DocumentService {

    private static final Logger LOGGER = Logger.getLogger(DocumentServiceImpl.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = "java:jboss/exported/jms/queue/test")
    private Queue indexJobQueue;

    @Inject
    private FolderService folderService;

    @Inject
    private DatabaseService databaseService;

    @Inject
    private FileReferenceService fileReferenceService;

    @Inject
    private UserService userService;

    @Inject
    private TagService tagService;

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TextDocument createDocument(TextDocument document, Folder inFolder) throws NotesException {

        try {

            LOGGER.info("create text-document");

            if (document == null) {
                throw new IllegalArgumentException("document is null");
            }

            if (inFolder == null || inFolder.getId() == 0) {
                inFolder = databaseService.getDatabaseOfUser().getDefaultFolder();
            } else {
                if (!em.contains(inFolder)) {
                    inFolder = folderService.getFolder(inFolder.getId());
                }
            }

            BasicDocument basicDocument = _createDocument(document, inFolder);

            indexDocument(basicDocument);

            Hibernate.initialize(basicDocument.getTags());
            return (TextDocument) basicDocument;

        } catch (Throwable t) {
            String message = String.format("Cannot run createDocument document=%s, inFolder=%s. Reason: %s", document, inFolder, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<BasicDocument> getDocumentsInFolder(Long folderId) throws NotesException {
        try {
            if (folderId == null || folderId <= 0) {
                throw new IllegalArgumentException(String.format("Invalid folder id '%s'", folderId));
            }

            Query query = em.createNamedQuery(BasicDocument.QUERY_IN_FOLDER);
            query.setParameter("ID", folderId);

            return (List<BasicDocument>) query.getResultList();

        } catch (Throwable t) {
            String message = String.format("Cannot run getDocumentsInFolder folderId=%s. Reason: %s", folderId, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BasicDocument getDocument(long documentId) throws NotesException {
        try {

            Query query = em.createNamedQuery(BasicDocument.QUERY_BY_ID);
            query.setParameter("ID", documentId);
            BasicDocument document = (BasicDocument) query.getSingleResult();
            Hibernate.initialize(document.getTags());
            return document;

        } catch (Throwable t) {
            String message = String.format("Cannot run getDocument documentId=%s, Reason: %s", documentId, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BasicDocument deleteDocument(long documentId) throws NotesException {
        try {

            BasicDocument document = _get(documentId);
//          todo replace through mdb  document.setTrigger(Trigger.DELETE);

            Session session = em.unwrap(Session.class);

            // update document count
            StandardFolder proxy = (StandardFolder) session.load(StandardFolder.class, document.getFolderId());
            proxy.setDocumentCount(proxy.getDocumentCount() - 1);
            em.merge(proxy);

            while (proxy.getParentId() != null) {
                StandardFolder parent = (StandardFolder) session.load(StandardFolder.class, proxy.getParentId());
                parent.setDocumentCount(parent.getDocumentCount() - 1);
                proxy = parent;
            }

            em.remove(document);

            Hibernate.initialize(document.getTags());

            return document;

        } catch (Throwable t) {
            String message = String.format("Cannot run deleteDocument documentId=%s, Reason: %s", documentId, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BasicDocument updateBasicDocument(BasicDocument ref) throws NotesException {
        try {

            if (ref == null) {
                throw new IllegalArgumentException("document is null");
            }

            BasicDocument document = _get(ref.getId());

            if (!equals(ref, document)) {

                copyAttributes(ref, document);

                indexDocument(document);

                em.merge(document);
                em.flush();
                em.refresh(document);
            }

            Hibernate.initialize(document.getTags());

            return document;

        } catch (Throwable t) {
            String message = String.format("Cannot run updateBasicDocument document=%s, Reason: %s", ref, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BasicDocument updateTextDocument(TextDocument ref) throws NotesException {
        try {

            if (ref == null) {
                throw new IllegalArgumentException("document is null");
            }

            TextDocument document = (TextDocument) _get(ref.getId());
            boolean similarText = StringUtils.equals(ref.getText(), document.getText());

            if (!equals(ref, document) || !similarText) {

                copyAttributes(ref, document);

                document.setText(ref.getText());

//                document.setTrigger(Trigger.INDEX);
                indexDocument(document);

                em.merge(document);
                em.flush();
                em.refresh(document);
            }

            Hibernate.initialize(document.getTags());

            return document;

        } catch (Throwable t) {
            String message = String.format("Cannot run updateTextDocument document=%s, Reason: %s", ref, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PdfDocument uploadDocument(List<FileItem> items) throws NotesException {
        try {
            FileReference reference = null;
            String title = null;

            Long folderId = NumberUtils.createLong(_getFieldValue("folderId", items));

            Folder inFolder;
            if (folderId == null || folderId == 0) {
                LOGGER.info("in default folder " + folderId);
                inFolder = databaseService.getDatabaseOfUser().getDefaultFolder();
            } else {
                LOGGER.info("in folder " + folderId);
                inFolder = folderService.getFolder(folderId);
            }

            if (inFolder == null) {
                throw new IllegalArgumentException("Invalid folder " + folderId);
            }

            for (FileItem item : items) {

                if (!item.isFormField()) {

                    reference = fileReferenceService.store(item);
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
//          todo replace with mdb document.setTrigger(Trigger.EXTRACT_PDF);

            return (PdfDocument) _createDocument(document, inFolder);

        } catch (Throwable t) {
            String message = String.format("Cannot run uploadDocument. Reason: %s", t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void moveTo(List<Long> documentIds, Long toFolderId) throws NotesException {

        try {

            if (documentIds == null || documentIds.isEmpty()) {
                throw new IllegalArgumentException("documentId is null or empty");
            }
            if (toFolderId == null) {
                throw new IllegalArgumentException("folderId is null or empty");
            }

            for (Long documentId : documentIds) {

                BasicDocument document = _get(documentId);

                //            todo check permissions

                Long fromFolderId = document.getFolderId();

                Session session = em.unwrap(Session.class);
                StandardFolder fromFolderProxy = (StandardFolder) session.load(StandardFolder.class, fromFolderId);
                if (fromFolderProxy == null) {
                    throw new IllegalArgumentException(String.format("folder with id %s is null", fromFolderId));
                }

                fromFolderProxy.getDocuments().remove(document);
                fromFolderProxy.setDocumentCount(fromFolderProxy.getDocumentCount() - 1);
                em.merge(fromFolderProxy);

                StandardFolder toFolderProxy = (StandardFolder) session.load(StandardFolder.class, toFolderId);
                if (toFolderProxy == null) {
                    throw new IllegalArgumentException(String.format("folder with id %s is null", toFolderId));
                }

                toFolderProxy.getDocuments().add(document);
                toFolderProxy.setDocumentCount(toFolderProxy.getDocumentCount() + 1);
                em.merge(toFolderProxy);

                //          todo re-index

            }
        } catch (Throwable t) {
            String message = String.format("Cannot run moveTo. documentIds=%s, folderId=%s. Reason: %s", StringUtils.join(documentIds, ", "), toFolderId, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        }

    }

    // -- Internal

    private void indexDocument(BasicDocument document) throws JMSException {
        Connection connection = null;

        try {

            connection = connectionFactory.createConnection();
            javax.jms.Session session = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
            MessageProducer publisher = session.createProducer(indexJobQueue);

            connection.start();

            LOGGER.info("trigger index " + document.getId());
            ObjectMessage message = session.createObjectMessage(document);
            publisher.send(message);

            publisher.close();

        } finally {


            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private BasicDocument _createDocument(BasicDocument document, Folder inFolder) throws NotesException {

        document.validate();

        em.persist(document);

        User user = userService.getUser(inFolder.getOwner());
        user.getDocuments().add(document);

        Session session = em.unwrap(Session.class);

        StandardFolder proxy = (StandardFolder) session.load(StandardFolder.class, inFolder.getId());
        if (proxy == null) {
            throw new IllegalArgumentException(String.format("folder with id %s is null", document.getFolderId()));
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

    private void copyAttributes(BasicDocument source, BasicDocument target) throws NotesException {
        target.setTags(resolveTags(target.getTags(), source.getTags()));
        target.setStar(source.isStar());
        target.setTitle(TextUtils.cleanHtml(source.getTitle()).replace("\n", ""));
    }

    private boolean equals(BasicDocument a, BasicDocument b) {
        boolean similarTitle = StringUtils.equals(b.getTitle(), a.getTitle());
        boolean similarStarState = b.isStar() == a.isStar();

        return similarTitle && equalsTags(b.getTags(), a.getTags()) && similarStarState;
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

        Map<String, StandardTag> cache = new HashMap<>();
        for (Tag c : cached) {
            cache.put(c.getName(), (StandardTag) c);
        }

        for (Tag t : tags) {
            if (cache.containsKey(t.getName())) {
                resolved.add(cache.get(t.getName()));
            } else {
                resolved.add(tagService.findOrCreate(t.getName()));
            }
        }

        return resolved;
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
