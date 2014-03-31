package org.notes.core.dao;

import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.core.interfaces.DatabaseManager;
import org.notes.core.interfaces.SessionData;
import org.notes.core.model.StandardDatabase;
import org.notes.core.model.StandardFolder;
import org.notes.core.model.User;

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
public class DatabaseManagerBean implements DatabaseManager {

    private static final Logger LOGGER = Logger.getLogger(DatabaseManagerBean.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private SessionData sessionData;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public StandardDatabase createDatabase(StandardDatabase database, User user) throws NotesException {
        try {
            if (database == null) {
                throw new NotesException("StandardDatabase is null");
            }
            if (user == null) {
                throw new NotesException("user is null");
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

        } catch (NotesException e) {
            throw e;
        } catch (Throwable t) {
            throw new NotesException("create database", t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public StandardDatabase getDatabase(long databaseId) throws NotesException {
        try {
            return _get(databaseId);

        } catch (NotesException e) {
            throw e;
        } catch (Throwable t) {
            throw new NotesException("get database " + databaseId, t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public StandardDatabase deleteDatabase(long databaseId) throws NotesException {
        try {
            return _delete(databaseId);

        } catch (NotesException e) {
            throw e;
        } catch (Throwable t) {
            throw new NotesException("delete database " + databaseId, t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public StandardDatabase getDatabaseOfUser() throws NotesException {
        try {
            Query query = em.createNamedQuery(StandardDatabase.QUERY_BY_USER);
            query.setParameter("USER", sessionData.getUser().getUsername());

            return (StandardDatabase) query.getSingleResult();

        } catch (Throwable t) {
            throw new NotesException("get database of user ", t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<StandardFolder> getFolders(long databaseId) throws NotesException {
        try {
            Query query = em.createNamedQuery(StandardFolder.QUERY_ROOT_FOLDERS);
            query.setParameter("OWNER", sessionData.getUser().getUsername());
            query.setParameter("DB_ID", databaseId);

            return query.getResultList();

        } catch (Throwable t) {
            throw new NotesException("get folders " + databaseId, t);
        }
    }

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
            throw new NotesException("set default folder", t);
        }
    }

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
            throw new NotesException("set trash folder", t);
        }
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public StandardDatabase updateDatabase(long databaseId, StandardDatabase database) throws NotesException {
        try {
            if (database == null) {
                throw new NotesException("database is null");
            }
            return _update(databaseId, database);

        } catch (Throwable t) {
            throw new NotesException("update database " + database.getId(), t);
        }
    }

    // -- Helper -- ----------------------------------------------------------------------------------------------------

    private StandardDatabase _get(Long databaseId) throws NotesException {

        if (databaseId == null || databaseId <= 0) {
            throw new NotesException(String.format("Invalid database id '%s'", databaseId));
        }

        Query query = em.createNamedQuery(StandardDatabase.QUERY_BY_ID);
        query.setParameter("ID", databaseId);

        List<StandardDatabase> databaseList = query.getResultList();
        if (databaseList.isEmpty()) {
            throw new NotesException(String.format("No database with id '%s' found", databaseId));
        }

        return databaseList.get(0);

    }

    private StandardDatabase _update(long databaseId, StandardDatabase newDatabase) throws NotesException {

        if (newDatabase == null) {
            throw new NotesException("Database is null");
        }

        StandardDatabase database = _get(databaseId);
        database.setModified(new Date());

        em.merge(database);
        em.flush();
        em.refresh(database);

        return database;

    }

    private StandardDatabase _delete(long databaseId) throws NotesException {
        StandardDatabase database = _get(databaseId);
        database.setDeleted(true);
        em.merge(database);

        return database;
    }

}
