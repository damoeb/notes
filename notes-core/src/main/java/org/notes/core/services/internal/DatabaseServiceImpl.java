package org.notes.core.services.internal;

import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.core.domain.SessionData;
import org.notes.core.domain.StandardDatabase;
import org.notes.core.domain.StandardFolder;
import org.notes.core.domain.User;
import org.notes.core.services.DatabaseService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

//@LocalBean
@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class DatabaseServiceImpl implements DatabaseService {

    private static final Logger LOGGER = Logger.getLogger(DatabaseServiceImpl.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private SessionData sessionData;

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public StandardDatabase createDatabase(StandardDatabase database, User user) throws NotesException {
        try {
            if (database == null) {
                throw new IllegalArgumentException("StandardDatabase is null");
            }
            if (user == null) {
                throw new IllegalArgumentException("user is null");
            }

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
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public StandardDatabase getDatabase(long databaseId) throws NotesException {
        try {
            return _get(databaseId);

        } catch (Throwable t) {
            String message = String.format("Cannot run getDatabase, databaseId=%s. Reason: %s", databaseId, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public StandardDatabase deleteDatabase(long databaseId) throws NotesException {
        try {
            return _delete(databaseId);

        } catch (Throwable t) {
            String message = String.format("Cannot run deleteDatabase, databaseId=%s. Reason: %s", databaseId, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public StandardDatabase getDatabaseOfUser() throws NotesException {
        try {
            Query query = em.createNamedQuery(StandardDatabase.QUERY_BY_USER);
            query.setParameter("USERNAME", sessionData.getUser().getUsername());

            return (StandardDatabase) query.getSingleResult();

        } catch (Throwable t) {
            String message = String.format("Cannot run getDatabaseOfUser. Reason: %s", t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<StandardFolder> getRootFolders(long databaseId) throws NotesException {
        try {
            Query query = em.createNamedQuery(StandardFolder.QUERY_ROOT_FOLDERS);
            query.setParameter("USERNAME", sessionData.getUser().getUsername());
            query.setParameter("DB_ID", databaseId);

            return query.getResultList();

        } catch (Throwable t) {
            String message = String.format("Cannot run getRootFolders, databaseId=%s. Reason: %s", databaseId, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void setDefaultFolder(StandardDatabase database, StandardFolder folder) throws NotesException {
        try {

            if (database == null) {
                throw new IllegalArgumentException("database is null");
            }

            if (folder == null) {
                throw new IllegalArgumentException("folder is null");
            }

            if (!em.contains(database)) {
                database = getDatabase(database.getId());
            }
            database.setDefaultFolder(folder);
            em.merge(database);

        } catch (Throwable t) {
            String message = String.format("Cannot run setDefaultFolder, database=%s, folder=%s. Reason: %s", database, folder, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void setTrashFolder(StandardDatabase database, StandardFolder folder) throws NotesException {
        try {

            if (database == null) {
                throw new IllegalArgumentException("database is null");
            }

            if (folder == null) {
                throw new IllegalArgumentException("folder is null");
            }

            if (!em.contains(database)) {
                database = getDatabase(database.getId());
            }
            database.setTrashFolder(folder);
            em.merge(database);

        } catch (Throwable t) {
            String message = String.format("Cannot run setTrashFolder, database=%s, folder=%s. Reason: %s", database, folder, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public StandardDatabase updateDatabase(long databaseId, StandardDatabase database) throws NotesException {
        try {
            if (database == null) {
                throw new IllegalArgumentException("database is null");
            }
            return _update(databaseId, database);

        } catch (Throwable t) {
            String message = String.format("Cannot run updateDatabase, databaseId=%s, database=%s. Reason: %s", databaseId, database, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        }
    }

    // -- Internal

    private StandardDatabase _get(Long databaseId) {

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

    private StandardDatabase _update(long databaseId, StandardDatabase newDatabase) {

        if (newDatabase == null) {
            throw new IllegalArgumentException("Database is null");
        }

        StandardDatabase database = _get(databaseId);
        database.setModified(new Date());

        em.merge(database);
        em.flush();
        em.refresh(database);

        return database;

    }

    private StandardDatabase _delete(long databaseId) {
        StandardDatabase database = _get(databaseId);
        database.setDeleted(true);
        em.merge(database);

        return database;
    }

}
