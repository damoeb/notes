package org.notes.core.dao;

import org.apache.log4j.Logger;
import org.hibernate.Session;
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
import java.io.Serializable;
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
    public Folder createFolder(Folder folder, Folder parent, Database database) throws NotesException {
        try {
            if (folder == null) {
                throw new NotesException("folder is null");
            }

            if (database == null) {
                throw new NotesException("database is null");
            }

            folder = _create(folder, parent);

            Database proxy = (Database) _getProxy(Database.class, database.getId());

            proxy.getFolders().add(folder);

            em.merge(proxy);

            return folder;

        } catch (NotesException e) {
            throw e;
        } catch (Throwable t) {
            throw new NotesException("create folder", t);
        }
    }

    private Object _getProxy(Class<? extends Object> clazz, Serializable id) throws NotesException {
        Session session = em.unwrap(Session.class);
        Object proxy = session.load(clazz, id);
        if (proxy == null) {
            throw new NotesException(String.format("Proxy object with id %s is null", id));
        }
        return proxy;
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
    public List<Folder> getChildren(long folderId) throws NotesException {
        try {

            Query query = em.createNamedQuery(Folder.QUERY_CHILDREN);
            query.setParameter("ID", folderId);

            return (List<Folder>) query.getResultList();

        } catch (Throwable t) {
            throw new NotesException("get children " + folderId, t);
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
    public Folder updateFolder(long folderId, Folder newFolder) throws NotesException {
        try {
            if (newFolder == null) {
                throw new NotesException("Folder is null");
            }
            /*
            todo
            check if parentId changed -> ..
              */

            Folder folder = _get(folderId);
            folder.setName(newFolder.getName());
            folder.setModified(new Date());
            folder.setExpanded(newFolder.isExpanded());
            em.merge(folder);
            em.flush();
            em.refresh(folder);

            return folder;

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

    private Folder _create(Folder folder, Folder parent) throws NotesException {

        if (folder == null) {
            throw new NotesException("Folder is null");
        }

        if (parent != null) {
            parent.getId();
            Folder parentProxy = (Folder) _getProxy(Folder.class, parent.getId());
            parentProxy.setLeaf(false);
            folder.setLevel(parentProxy.getLevel() + 1);
            em.merge(parentProxy);
        } else {
            folder.setLevel(0);
        }

        folder.setParent(parent);
        folder.setModified(new Date());

        em.persist(folder);
        em.flush();
        em.refresh(folder);

        User user = (User) _getProxy(User.class, 1l);
        user.getFolders().add(folder);
        em.merge(user);

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
