package org.notes.core.services.internal;

import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.core.domain.NotesSession;
import org.notes.core.domain.StandardDatabase;
import org.notes.core.domain.StandardFolder;
import org.notes.core.domain.User;
import org.notes.core.services.DatabaseService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

//@LocalBean
@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class DatabaseServiceImpl implements DatabaseService {

    private static final Logger LOGGER = Logger.getLogger(DatabaseServiceImpl.class);

    @PersistenceUnit(unitName = "primary")
    private EntityManagerFactory emf;

    @Inject
    private NotesSession notesSession;

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public StandardDatabase createDatabase(StandardDatabase database, User user) throws NotesException {
        EntityManager em = null;

        try {
            if (database == null) {
                throw new IllegalArgumentException("StandardDatabase is null");
            }
            if (user == null) {
                throw new IllegalArgumentException("user is null");
            }

            em = emf.createEntityManager();

            if (!em.contains(user)) {
                Query query = em.createNamedQuery(User.QUERY_BY_ID);
                query.setParameter("USERNAME", user.getUsername());
                user = (User) query.getSingleResult();
            }

            database.setDocumentCount(0);
            database.setModified(new Date());

            em.persist(database);
            user.getDatabases().add(database);
            em.merge(user);
            em.flush();
            em.refresh(database);

            return database;

        } catch (Throwable t) {
            String message = String.format("Cannot run createDatabase, database=%s, user=%s. Reason: %s", database, user, t.getMessage());
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
    public StandardDatabase getDatabase(long databaseId) throws NotesException {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();

            return _get(em, databaseId);

        } catch (Throwable t) {
            String message = String.format("Cannot run getDatabase, databaseId=%s. Reason: %s", databaseId, t.getMessage());
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
    public StandardDatabase deleteDatabase(long databaseId) throws NotesException {
        EntityManager em = null;

        try {
            em = emf.createEntityManager();

            // todo implement delete
            StandardDatabase database = _get(em, databaseId);
            database.setDeleted(true);
            em.merge(database);

            return database;

        } catch (Throwable t) {
            String message = String.format("Cannot run deleteDatabase, databaseId=%s. Reason: %s", databaseId, t.getMessage());
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
    public StandardDatabase getDatabaseOfUser() throws NotesException {
        EntityManager em = null;

        try {
            em = emf.createEntityManager();

            Query query = em.createNamedQuery(StandardDatabase.QUERY_BY_USER);
            query.setParameter("USERNAME", notesSession.getUserId());

            return (StandardDatabase) query.getSingleResult();

        } catch (Throwable t) {
            String message = String.format("Cannot run getDatabaseOfUser. Reason: %s", t.getMessage());
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
    public List<StandardFolder> getRootFolders(long databaseId) throws NotesException {
        EntityManager em = null;

        try {
            em = emf.createEntityManager();

            Query query = em.createNamedQuery(StandardFolder.QUERY_ROOT_FOLDERS);
            query.setParameter("USERNAME", notesSession.getUserId());
            query.setParameter("DB_ID", databaseId);

            return query.getResultList();

        } catch (Throwable t) {
            String message = String.format("Cannot run getRootFolders, databaseId=%s. Reason: %s", databaseId, t.getMessage());
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
    public void setDefaultFolder(StandardDatabase database, StandardFolder folder) throws NotesException {
        EntityManager em = null;

        try {

            if (database == null) {
                throw new IllegalArgumentException("database is null");
            }

            if (folder == null) {
                throw new IllegalArgumentException("folder is null");
            }

            em = emf.createEntityManager();

            if (!em.contains(database)) {
                database = getDatabase(database.getId());
            }
            database.setDefaultFolder(folder);
            em.merge(database);

        } catch (Throwable t) {
            String message = String.format("Cannot run setDefaultFolder, database=%s, folder=%s. Reason: %s", database, folder, t.getMessage());
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
    public void setTrashFolder(StandardDatabase database, StandardFolder folder) throws NotesException {
        EntityManager em = null;

        try {

            if (database == null) {
                throw new IllegalArgumentException("database is null");
            }

            if (folder == null) {
                throw new IllegalArgumentException("folder is null");
            }

            em = emf.createEntityManager();

            if (!em.contains(database)) {
                database = getDatabase(database.getId());
            }
            database.setTrashFolder(folder);
            em.merge(database);

        } catch (Throwable t) {
            String message = String.format("Cannot run setTrashFolder, database=%s, folder=%s. Reason: %s", database, folder, t.getMessage());
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
    public StandardDatabase updateDatabase(long databaseId, StandardDatabase database) throws NotesException {
        EntityManager em = null;

        try {
            if (database == null) {
                throw new IllegalArgumentException("database is null");
            }
            em = emf.createEntityManager();

            // todo does not work
            // todo test that user owns all these folder ids
            StandardDatabase original = _get(em, databaseId);
            original.setModified(new Date());
//            original.setActiveFolderId(database.getActiveFolderId());
//            original.setDefaultFolderId(database.getDefaultFolderId());
//            original.setTrashFolderId(database.getTrashFolderId());

            em.merge(original);
            em.flush();
            em.refresh(original);

            return original;

        } catch (Throwable t) {
            String message = String.format("Cannot run updateDatabase, databaseId=%s, database=%s. Reason: %s", databaseId, database, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    // -- Internal

    private StandardDatabase _get(EntityManager em, Long databaseId) {

        if (databaseId == null || databaseId <= 0) {
            throw new IllegalArgumentException(String.format("Invalid database id '%s'", databaseId));
        }

        Query query = em.createNamedQuery(StandardDatabase.QUERY_BY_ID);
        query.setParameter("ID", databaseId);

        List<StandardDatabase> databaseList = query.getResultList();
        if (databaseList.isEmpty()) {
            throw new IllegalArgumentException(String.format("No database with id '%s' found", databaseId));
        }

        return databaseList.get(0);

    }
}
