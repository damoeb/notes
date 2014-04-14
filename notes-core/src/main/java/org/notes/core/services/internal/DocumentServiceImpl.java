package org.notes.core.services.internal;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.domain.Document;
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
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import java.util.*;

//@LocalBean
@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class DocumentServiceImpl implements DocumentService {

    private static final Logger LOGGER = Logger.getLogger(DocumentServiceImpl.class);

    @PersistenceUnit(unitName = "primary")
    private EntityManagerFactory emf;

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

    @Inject
    private NotesSession notesSession;

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public TextDocument createDocument(TextDocument document, Folder inFolder) throws NotesException {

        EntityManager em = null;

        try {
            if (document == null) {
                throw new IllegalArgumentException("document is null");
            }
            em = emf.createEntityManager();

            LOGGER.info("create text-document");

            if (inFolder == null || inFolder.getId() == 0) {
                inFolder = folderService.getFolder(notesSession.getDefaultFolderId());
            } else {
                inFolder = folderService.getFolder(inFolder.getId());
            }

            BasicDocument basicDocument = _createDocument(em, document, notesSession.getUserId(), inFolder);

            triggerPostProcess(EventType.INDEX, basicDocument);

            return (TextDocument) basicDocument;

        } catch (Throwable t) {
            String message = String.format("Cannot run createDocument document=%s, inFolder=%s. Reason: %s", document, inFolder, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<BasicDocument> getDocumentsInFolder(Long folderId, int start, int rows) throws NotesException {
        EntityManager em = null;

        try {
            em = emf.createEntityManager();

            if (folderId == null || folderId <= 0) {
                throw new IllegalArgumentException(String.format("Invalid folder id '%s'", folderId));
            }

            if (start < 0) {
                throw new IllegalArgumentException(String.format("start < 0. start='%s'", start));
            }
            if (rows <= 0) {
                throw new IllegalArgumentException(String.format("rows < 1. rows='%s'", rows));
            }
            if (rows > 30) {
                throw new IllegalArgumentException(String.format("rows > MAX. rows='%s'", rows));
            }

            Query query = em.createNamedQuery(BasicDocument.QUERY_IN_FOLDER);
            query.setParameter("ID", folderId);
            query.setMaxResults(rows + 1);
            query.setFirstResult(start);

            return (List<BasicDocument>) query.getResultList();

        } catch (Throwable t) {
            String message = String.format("Cannot run getDocumentsInFolder folderId=%s. Reason: %s", folderId, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BasicDocument getDocument(long documentId) throws NotesException {
        EntityManager em = null;

        try {
            em = emf.createEntityManager();

            Query query = em.createNamedQuery(BasicDocument.QUERY_BY_ID);
            query.setParameter("ID", documentId);
            BasicDocument document = (BasicDocument) query.getSingleResult();
            Hibernate.initialize(document.getTags());
            return document;

        } catch (Throwable t) {
            String message = String.format("Cannot run getDocument documentId=%s, Reason: %s", documentId, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteDocument(long documentId) throws NotesException {
        EntityManager em = null;

        try {
            em = emf.createEntityManager();


            BasicDocument document = _get(documentId, em);
            triggerPostProcess(EventType.UN_INDEX, document);

            updateDocumentCount(em, document.getFolder(), -1);

            em.remove(document);

        } catch (Throwable t) {
            String message = String.format("Cannot run deleteDocument documentId=%s, Reason: %s", documentId, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(List<Long> documentIds) throws NotesException {
        try {

            // todo improve: this takes forever
            for (Long id : documentIds) {
                deleteDocument(id);
            }

        } catch (Throwable t) {
            String message = String.format("Cannot run delete, ids=%s, Reason: %s", StringUtils.join(documentIds, ", "), t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BasicDocument updateBasicDocument(BasicDocument document) throws NotesException {
        EntityManager em = null;

        try {
            if (document == null) {
                throw new IllegalArgumentException("document is null");
            }

            em = emf.createEntityManager();

            BasicDocument original = _get(document.getId(), em);

            if (!equals(document, original)) {

                copyAttributes(document, original);

                triggerPostProcess(EventType.INDEX, original);

                em.merge(original);
                em.flush();
                em.refresh(original);
            }

            Hibernate.initialize(original.getTags());

            return original;

        } catch (Throwable t) {
            String message = String.format("Cannot run updateBasicDocument document=%s, Reason: %s", document, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BasicDocument updateTextDocument(TextDocument document) throws NotesException {
        EntityManager em = null;

        try {
            if (document == null) {
                throw new IllegalArgumentException("document is null");
            }

            em = emf.createEntityManager();

            TextDocument original = (TextDocument) _get(document.getId(), em);
            boolean similarText = StringUtils.equals(document.getText(), original.getText());

            if (!equals(document, original) || !similarText) {

                copyAttributes(document, original);

                original.setText(document.getText());

                triggerPostProcess(EventType.INDEX, original);

                em.merge(original);
                em.flush();
                em.refresh(original);
            }

            Hibernate.initialize(original.getTags());

            return original;

        } catch (Throwable t) {
            String message = String.format("Cannot run updateTextDocument document=%s, Reason: %s", document, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PdfDocument uploadDocument(List<FileItem> items) throws NotesException {
        EntityManager em = null;

        try {
            em = emf.createEntityManager();

            FileReference reference = null;
            String title = null;

            Long folderId = NumberUtils.createLong(_getFieldValue("folderId", items));

            Folder inFolder;
            if (folderId == null || folderId == 0) {
                LOGGER.info("in default folder " + folderId);
                inFolder = folderService.getFolder(notesSession.getDefaultFolderId());
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

            triggerPostProcess(EventType.EXTRACT_TEXT, document);

            return (PdfDocument) _createDocument(em, document, notesSession.getUserId(), inFolder);

        } catch (Throwable t) {
            String message = String.format("Cannot run uploadDocument. Reason: %s", t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void moveTo(List<Long> documentIds, Long toFolderId) throws NotesException {

        EntityManager em = null;

        try {
            if (documentIds == null || documentIds.isEmpty()) {
                throw new IllegalArgumentException("documentId is null or empty");
            }
            if (toFolderId == null) {
                throw new IllegalArgumentException("folderId is null or empty");
            }

            em = emf.createEntityManager();

            // todo check permissions on toFolderId

            // unique
            Set<Long> ids = new HashSet<>(documentIds.size());
            ids.addAll(documentIds);

            BasicDocument document;
            Long fromFolderId = null;

            Set<Document> affected = new HashSet<>(documentIds.size());


            Session session = em.unwrap(Session.class);

            Folder toFolder = (Folder) session.load(StandardFolder.class, toFolderId);
            Folder fromFolder = null;

            for (Long documentId : ids) {

                document = _get(documentId, em);

                affected.add(document);

                // todo check permissions

                if (fromFolderId == null) {
                    fromFolderId = document.getFolderId();
                    fromFolder = (Folder) session.load(StandardFolder.class, fromFolderId);
                } else {
                    if (!fromFolderId.equals(document.getFolderId())) {
                        throw new IllegalArgumentException("All documents are supposed to be in the same source folder");
                    }
                }

                document.setFolder(toFolder);
            }

            // update doc counter
            updateDocumentCount(em, toFolder, ids.size());
            updateDocumentCount(em, fromFolder, -1 * ids.size());

            triggerPostProcess(EventType.INDEX, affected);

        } catch (Throwable t) {
            String message = String.format("Cannot run moveTo. documentIds=%s, folderId=%s. Reason: %s", StringUtils.join(documentIds, ", "), toFolderId, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    // -- Internal

    private void updateDocumentCount(EntityManager em, Folder folder, int delta) {

        while (folder != null) {
            folder.setDocumentCount(folder.getDocumentCount() + delta);
            em.merge(folder);

            folder = folder.getParent();
        }

    }


    private void triggerPostProcess(EventType type, Document document) throws JMSException {
        triggerPostProcess(type, Arrays.asList(document));
    }

    private void triggerPostProcess(EventType type, Collection<Document> documents) throws JMSException {
        Connection connection = null;

        try {

            connection = connectionFactory.createConnection();
            javax.jms.Session session = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
            MessageProducer publisher = session.createProducer(indexJobQueue);

            connection.start();

            ObjectMessage message = session.createObjectMessage(new PostProcessEvent(documents, type));
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

    private BasicDocument _createDocument(EntityManager em, BasicDocument document, String userId, Folder folder) throws NotesException {

        document.validate();

        document.setUserId(userId);
        document.setFolder(folder);

        //--

        updateDocumentCount(em, folder, 1);

        em.persist(document);
        em.refresh(document);

        return document;
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

    private BasicDocument _get(long documentId, EntityManager entityManager) throws NotesException {
        return entityManager.find(BasicDocument.class, documentId);
    }
}
