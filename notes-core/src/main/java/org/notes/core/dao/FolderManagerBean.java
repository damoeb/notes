package org.notes.core.dao;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.core.interfaces.DatabaseManager;
import org.notes.core.interfaces.FolderManager;
import org.notes.core.interfaces.UserManager;
import org.notes.core.model.Database;
import org.notes.core.model.Folder;
import org.notes.core.request.NotesRequestException;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

//@LocalBean
@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class FolderManagerBean implements FolderManager {

    private static final Logger LOGGER = Logger.getLogger(FolderManagerBean.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private UserManager userManager;

    @Inject
    private DatabaseManager databaseManager;


    // -- Folder -- ----------------------------------------------------------------------------------------------------

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Folder createFolder(Folder folder) throws NotesException {
        try {
            if (folder.getDatabaseId() == null) {
                throw new NotesException("databaseId is null");
            }

            Database database = databaseManager.getDatabase(folder.getDatabaseId());
            Hibernate.initialize(database.getFolders());

            folder = _create(folder);

            database.getFolders().add(folder);
            em.merge(database);

            return folder;

        } catch (NotesException e) {
            throw e;
        } catch (Throwable t) {
            throw new NotesRequestException("create database", t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Folder getFolder(long folderId) throws NotesException {
        try {
            return _get(folderId);

        } catch (NotesException e) {
            throw e;
        } catch (Throwable t) {
            throw new NotesException("get database " + folderId, t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Folder deleteFolder(long folderId) throws NotesException {
        try {
            return _delete(folderId);

        } catch (NotesException e) {
            throw e;
        } catch (Throwable t) {
            throw new NotesException("delete database " + folderId, t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Folder updateFolder(long folderId, Folder newFolder) throws NotesException {
        try {
            return _update(folderId, newFolder);

        } catch (Throwable t) {
            throw new NotesException("update database " + folderId, t);
        }
    }


    // -- Helper -- ----------------------------------------------------------------------------------------------------

    private Folder _get(Long folderId) throws NotesException {

        if (folderId == null || folderId <= 0) {
            throw new NotesException(String.format("Invalid folder id '%s'", folderId));
        }

        Query query = em.createNamedQuery(Folder.QUERY_BY_ID);
        query.setParameter("ID", folderId);

        List<Folder> folderList = query.getResultList();
        if (folderList.isEmpty()) {
            throw new NotesException(String.format("No folder with id '%s' found", folderId));
        }

        return folderList.get(0);

    }

    private Folder _create(Folder folder) throws NotesException {

        if (folder == null) {
            throw new NotesException("Folder is null");
        }

        folder.setOwnerId(1l); // todo userId

        em.persist(folder);
        em.flush();
        em.refresh(folder);

        return folder;

    }

    private Folder _update(long folderId, Folder newFolder) throws NotesException {

        if (newFolder == null) {
            throw new NotesException("Folder is null");
        }

        Folder folder = _get(folderId);
        folder.setName(newFolder.getName());
        em.merge(folder);
        em.flush();
        em.refresh(folder);

        return folder;

    }

    private Folder _delete(long folderId) throws NotesException {
        Folder database = _get(folderId);
        database.setDeleted(true);
        em.merge(database);

        return database;
    }

}
