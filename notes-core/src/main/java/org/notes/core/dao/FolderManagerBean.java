package org.notes.core.dao;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.core.interfaces.DatabaseManager;
import org.notes.core.interfaces.FolderManager;
import org.notes.core.interfaces.UserManager;
import org.notes.core.model.BasicDocument;
import org.notes.core.model.Database;
import org.notes.core.model.Folder;
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
public class FolderManagerBean implements FolderManager {

    private static final Logger LOGGER = Logger.getLogger(FolderManagerBean.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private UserManager userManager;

    @Inject
    private DatabaseManager databaseManager;


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

            database.setSelectedFolderId(folder.getId());

            database.getFolders().add(folder);
            em.merge(database);

            return folder;

        } catch (NotesException e) {
            throw e;
        } catch (Throwable t) {
            throw new NotesException("create folder", t);
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
            throw new NotesException("get folder " + folderId, t);
        }
    }

    @Override
    public List<BasicDocument> getDocuments(Long folderId) throws NotesException {
        try {
            if (folderId == null || folderId <= 0) {
                throw new NotesException(String.format("Invalid folder id '%s'", folderId));
            }

            Query query = em.createNamedQuery(Folder.QUERY_DOCUMENTS);
            query.setParameter("ID", folderId);

            return (List<BasicDocument>) query.getResultList();

        } catch (NotesException e) {
            throw e;
        } catch (Throwable t) {
            throw new NotesException("get documents of " + folderId, t);
        }
    }

    @Override
    public List<BasicDocument> getRelatedDocuments(Long folderId, int offset, int count) throws NotesException {
        try {
            if (folderId == null || folderId <= 0) {
                throw new NotesException(String.format("Invalid folder id '%s'", folderId));
            }

            if (offset < 0) {
                offset = 0;
            }
            if (count <= 0) {
                count = 100;
            }
            if (count > 100) {
                count = 100;
            }
            Query query = em.createNamedQuery(Folder.QUERY_RELATED_DOCUMENTS);
            query.setFirstResult(offset);
            query.setMaxResults(count);

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
    public Folder deleteFolder(Folder folder) throws NotesException {
        try {
            return _delete(folder);

        } catch (NotesException e) {
            throw e;
        } catch (Throwable t) {
            throw new NotesException("delete folder " + folder, t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Folder updateFolder(Folder newFolder) throws NotesException {
        try {
            return _update(newFolder);

        } catch (Throwable t) {
            throw new NotesException("update folder: " + t.getMessage(), t);
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

        Folder parent = null;
        if (folder.getParentId() == null) {
            folder.setLevel(0);
        } else {
            parent = _get(folder.getParentId());
            parent.setLeaf(false);
            parent.setExpanded(true);
            em.merge(parent);
            folder.setLevel(parent.getLevel() + 1);
        }

        User user = userManager.getUser(1l);
        folder.setParent(parent);
        folder.setModified(new Date());

        em.persist(folder);
        em.flush();
        em.refresh(folder);

        user.getFolders().add(folder);
        em.merge(user);

        return folder;

    }

    private Folder _update(Folder newFolder) throws NotesException {

        if (newFolder == null) {
            throw new NotesException("Folder is null");
        }
        if (newFolder.getId() <= 0) {
            throw new NotesException("Folder Id is invalid");
        }

        /*
        todo
        check if parentId changed -> ..
          */

        Folder folder = _get(newFolder.getId());
        folder.setName(newFolder.getName());
        folder.setModified(new Date());
        folder.setExpanded(newFolder.isExpanded());
        em.merge(folder);
        em.flush();
        em.refresh(folder);

        return folder;

    }

    private Folder _delete(Folder folder) throws NotesException {
        if (folder == null) {
            throw new NotesException("Folder is null");
        }
        if (folder.getId() <= 0) {
            throw new NotesException("Folder Id is invalid");
        }

        folder = _get(folder.getId());
        folder.setDeleted(true);
        em.merge(folder);

        return folder;
    }

}
