package org.notes.core.dao;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.core.interfaces.DatabaseManager;
import org.notes.core.interfaces.UserManager;
import org.notes.core.model.Database;
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
    private UserManager userManager;


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Database createDatabase(Database database) throws NotesException {
        try {
            return _create(database);

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
            // todo only folders that are open or direct children of opened folders
            Hibernate.initialize(database.getFolders());
            return database;

        } catch (NotesException e) {
            throw e;
        } catch (Throwable t) {
            throw new NotesException("get database " + databaseId, t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Database deleteDatabase(Database database) throws NotesException {
        try {
            if (database == null) {
                throw new NotesException("database is null");
            }
            return _delete(database.getId());

        } catch (NotesException e) {
            throw e;
        } catch (Throwable t) {
            throw new NotesException("delete database " + database, t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Database> getDatabases() throws NotesException {
        try {
            Query query = em.createNamedQuery(Database.QUERY_ALL);
            query.setParameter("USER_ID", 1l);  // todo userId

            return query.getResultList();

        } catch (Throwable t) {
            throw new NotesException("get databases ", t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Database updateDatabase(Database database) throws NotesException {
        try {
            if (database == null) {
                throw new NotesException("database is null");
            }
            return _update(database.getId(), database);

        } catch (Throwable t) {
            throw new NotesException("update database " + database.getId(), t);
        }
    }

    // -- Helper -- ----------------------------------------------------------------------------------------------------

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

    private Database _create(Database database) throws NotesException {

        if (database == null) {
            throw new NotesException("Database is null");
        }

        database.setDocumentCount(0);
        database.setModified(new Date());

        User user = userManager.getUser(1l);
        em.persist(database);
        em.flush();
        em.refresh(database);
        user.getDatabases().add(database);
        em.merge(user);

        return database;

    }

    private Database _update(long databaseId, Database newDatabase) throws NotesException {

        if (newDatabase == null) {
            throw new NotesException("Database is null");
        }

        Database database = _get(databaseId);
        database.setName(newDatabase.getName());
        database.setSelectedFolderId(newDatabase.getSelectedFolderId());
        database.setModified(new Date());
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
