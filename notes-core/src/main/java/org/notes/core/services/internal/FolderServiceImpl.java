package org.notes.core.services.internal;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.domain.Database;
import org.notes.common.domain.Document;
import org.notes.common.domain.Folder;
import org.notes.common.exceptions.NotesException;
import org.notes.common.services.FolderService;
import org.notes.core.domain.StandardDatabase;
import org.notes.core.domain.StandardFolder;
import org.notes.core.domain.User;
import org.notes.core.services.DatabaseService;
import org.notes.core.services.UserService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

//@LocalBean
@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class FolderServiceImpl implements FolderService {

    private static final Logger LOGGER = Logger.getLogger(FolderServiceImpl.class);

    @PersistenceUnit(unitName = "primary")
    private EntityManagerFactory emf;

    @Inject
    private UserService userService;

    @Inject
    private DatabaseService databaseService;

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Folder createFolder(Folder folder, Folder parent, Database database) throws NotesException {
        EntityManager em = null;

        try {
            if (folder == null) {
                throw new IllegalArgumentException("folder is null");
            }

            if (database == null) {
                throw new IllegalArgumentException("database is null");
            }

            em = emf.createEntityManager();

            database = databaseService.getDatabase(database.getId());

            if (parent != null && !em.contains(parent)) {
                parent = _get(em, parent.getId());
            }

            folder = _create(em, folder, parent, database);

            // use database as proxy
            Database proxy = (Database) _getProxy(em, StandardDatabase.class, database.getId());
            proxy.getFolders().add(folder);

            em.merge(proxy);
            em.flush();
            em.refresh(folder);

            return folder;

        } catch (Throwable t) {
            String message = String.format("Cannot run createFolder, folder=%s, parent=%s, database:%s. Reason: %s", folder, parent, database, t.getMessage());
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
    public StandardFolder getFolder(long folderId) throws NotesException {
        EntityManager em = null;

        try {
            em = emf.createEntityManager();

            // todo validate req
            return _get(em, folderId);

        } catch (Throwable t) {
            String message = String.format("Cannot run getFolder, folderId=%s. Reason: %s", folderId, t.getMessage());
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
    public List<Folder> getChildren(long folderId) throws NotesException {
        EntityManager em = null;

        try {
            em = emf.createEntityManager();

            Query query = em.createNamedQuery(StandardFolder.QUERY_CHILDREN);
            query.setParameter("ID", folderId);

            return (List<Folder>) query.getResultList();

        } catch (Throwable t) {
            String message = String.format("Cannot run getChildren, folderId=%s. Reason: %s", folderId, t.getMessage());
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
    public List<Folder> getParents(Document document) throws NotesException {
        EntityManager em = null;

        try {
            if (document == null) {
                throw new IllegalArgumentException("document is null");
            }

            em = emf.createEntityManager();

            List<Folder> parents = new LinkedList<>();

            Long folderId = document.getFolderId();
            while (true) {

                Folder folder = _get(em, folderId);
                Folder parent = folder.getParent();

                if (parent == null) {
                    break;
                }

                folderId = parent.getId();

                parents.add(folder);
            }

            return parents;

        } catch (Throwable t) {
            String message = String.format("Cannot run getParents, document=%s. Reason: %s", document, t.getMessage());
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
    public void deleteFolder(long folderId) throws NotesException {
        EntityManager em = null;

        try {

            if (folderId <= 0) {
                throw new IllegalArgumentException("folderId is invalid");
            }
            em = emf.createEntityManager();

            // todo check if folder is immutable (e.g. trash, untitled)

            StandardFolder folder = _get(em, folderId);

            if (folder == null) {
                throw new IllegalArgumentException("folder is null");
            }

            int delta = folder.getDocumentCount();

            Folder parent = folder.getParent();
            while (parent != null) {
                parent.setDocumentCount(parent.getDocumentCount() - delta);
                em.merge(parent);
                parent = parent.getParent();
            }

            User user = folder.getUser();
            user.setDocumentCount(user.getDocumentCount() - delta);
            user.setFolderCount(user.getFolderCount() - 1);
            em.merge(user);

            em.remove(folder);

        } catch (Throwable t) {
            String message = String.format("Cannot run deleteFolder, folderId=%s. Reason: %s", folderId, t.getMessage());
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
    public StandardFolder updateFolder(long folderId, Folder newFolder) throws NotesException {
        EntityManager em = null;

        try {
            if (newFolder == null) {
                throw new NotesException("Folder is null");
            }

            em = emf.createEntityManager();

            StandardFolder folder = _get(em, folderId);
            folder.setName(newFolder.getName());
            folder.setModified(new Date());
            folder.setExpanded(newFolder.isExpanded());

            em.merge(folder);
            em.flush();
            em.refresh(folder);

            return folder;

        } catch (Throwable t) {
            String message = String.format("Cannot run updateFolder, folderId=%s, newFolder=%s. Reason: %s", folderId, newFolder, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }


    // -- Internal

    private Object _getProxy(EntityManager em, Class<? extends Object> clazz, Serializable id) {
        Session session = em.unwrap(Session.class);
        Object proxy = session.load(clazz, id);
        if (proxy == null) {
            throw new IllegalArgumentException(String.format("Proxy object with id %s is null", id));
        }
        return proxy;
    }

    private StandardFolder _get(EntityManager em, Long folderId) {

        if (folderId == null || folderId <= 0) {
            throw new IllegalArgumentException(String.format("Invalid folder id '%s'", folderId));
        }

        Query query = em.createNamedQuery(StandardFolder.QUERY_BY_ID);
        query.setParameter("ID", folderId);

        // todo replace by getSingleResult
        List<StandardFolder> folderList = query.getResultList();
        if (folderList.isEmpty()) {
            throw new IllegalArgumentException(String.format("No folder with id '%s' found", folderId));
        }

        return folderList.get(0);

    }

    private Folder _create(EntityManager em, Folder folder, Folder parent, Database database) {

        if (folder == null) {
            throw new IllegalArgumentException("Folder is null");
        }

        if (parent != null) {

            if (parent.getLevel() >= 5) {
                throw new IllegalArgumentException("Max folder depth reached.");
            }

            parent.setLeaf(false);
            folder.setLevel(parent.getLevel() + 1);
            em.merge(parent);
        } else {
            folder.setLevel(0);
        }

        folder.setParent(parent);
        folder.setModified(new Date());

        em.persist(folder);
        em.flush();
        em.refresh(folder);

        User user = (User) _getProxy(em, User.class, database.getUserId());
        user.getFolders().add(folder);
        em.merge(user);

        return folder;

    }
}
