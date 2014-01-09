package org.notes.core.dao;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.core.interfaces.DatabaseManager;
import org.notes.core.interfaces.SessionData;
import org.notes.core.model.Database;
import org.notes.core.model.Folder;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public Database createDatabase(Database database) throws NotesException {
        try {
            if (database == null) {
                throw new NotesException("Database is null");
            }

            database.setDocumentCount(0);
            database.setModified(new Date());

            em.persist(database);
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
    public Database getDatabase(long databaseId) throws NotesException {
        try {
            Database database = _get(databaseId);

            return database;

        } catch (NotesException e) {
            throw e;
        } catch (Throwable t) {
            throw new NotesException("get database " + databaseId, t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Database deleteDatabase(long databaseId) throws NotesException {
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
    public List<Database> getDatabasesOfCurrentUser() throws NotesException {
        try {
            Query query = em.createNamedQuery(Database.QUERY_BY_USER);
            query.setParameter("USER", sessionData.getUser().getUsername());

            return query.getResultList();

        } catch (Throwable t) {
            throw new NotesException("get databases of user ", t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Folder> getFolders(long databaseId) throws NotesException {
        try {
            Query query = em.createNamedQuery(Folder.QUERY_ROOT_FOLDERS);
            query.setParameter("OWNER", sessionData.getUser().getUsername());
            query.setParameter("DB_ID", databaseId);

            return query.getResultList();

        } catch (Throwable t) {
            throw new NotesException("get folders " + databaseId, t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Folder> getOpenFolders(long databaseId) throws NotesException {
        try {
            Query query = em.createNamedQuery(Folder.QUERY_OPEN_FOLDERS);
            query.setParameter("OWNER", sessionData.getUser().getUsername());
            query.setParameter("DB_ID", databaseId);

            return query.getResultList();

        } catch (Throwable t) {
            throw new NotesException("get open folders " + databaseId, t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Database updateDatabase(long databaseId, Database database) throws NotesException {
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

//    private SessionData getUserSettings() throws NamingException {
//
//        InitialContext ic = new InitialContext();
//        SessionData bean = (SessionData)
//                ic.lookup("java:comp/env/SessionDataBean");
//        return bean;
//    }

    private Database _get(Long databaseId) throws NotesException {

        if (databaseId == null || databaseId <= 0) {
            throw new NotesException(String.format("Invalid database id '%s'", databaseId));
        }

        Query query = em.createNamedQuery(Database.QUERY_BY_ID);
        query.setParameter("ID", databaseId);

        List<Database> databaseList = query.getResultList();
        if (databaseList.isEmpty()) {
            throw new NotesException(String.format("No database with id '%s' found", databaseId));
        }

        return databaseList.get(0);

    }

    private Database _update(long databaseId, Database newDatabase) throws NotesException {

        if (newDatabase == null) {
            throw new NotesException("Database is null");
        }

        Database database = _get(databaseId);
        database.setName(newDatabase.getName());
        database.setModified(new Date());

        Set<Folder> folders = new HashSet(newDatabase.getOpenFolders().size());
        folders.clear();

        Session session = em.unwrap(Session.class);

        for (Folder unresolved : newDatabase.getOpenFolders()) {
            // todo check permissions
            Folder folder = (Folder) session.load(Folder.class, unresolved.getId());
            folders.add(folder);
        }
        database.setOpenFolders(folders);

        em.merge(database);
        em.flush();
        em.refresh(database);

        return database;

    }

    private Database _delete(long databaseId) throws NotesException {
        Database database = _get(databaseId);
        database.setDeleted(true);
        em.merge(database);

        return database;
    }

}
