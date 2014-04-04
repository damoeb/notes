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
import javax.persistence.PersistenceContext;
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

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private UserService userService;

    @Inject
    private DatabaseService databaseService;

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

            if (!em.contains(database)) {
                database = databaseService.getDatabase(database.getId());
            }

            if (parent != null && !em.contains(parent)) {
                parent = _get(parent.getId());
            }

            folder = _create(folder, parent, database);

            // use database as proxy
            Database proxy = (Database) _getProxy(StandardDatabase.class, database.getId());
            proxy.getFolders().add(folder);

            em.merge(proxy);
            em.flush();
            em.refresh(folder);

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
    public StandardFolder getFolder(long folderId) throws NotesException {
        try {

            // todo validate req
            return _get(folderId);

        } catch (NotesException e) {
            throw e;
        } catch (Throwable t) {
            throw new NotesException("get folder " + folderId, t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Folder> getChildren(long folderId) throws NotesException {
        try {

            Query query = em.createNamedQuery(StandardFolder.QUERY_CHILDREN);
            query.setParameter("ID", folderId);

            return (List<Folder>) query.getResultList();

        } catch (Throwable t) {
            throw new NotesException("get children " + folderId, t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<Folder> getParents(Document document) throws NotesException {
        try {

            if (document == null) {
                throw new IllegalArgumentException("document is null");
            }

            List<Folder> parents = new LinkedList<>();

            Long folderId = document.getFolderId();
            while (folderId != null) {

                Folder parent = getFolder(folderId);
                folderId = parent.getId();

                parents.add(parent);
            }

            return parents;
        } catch (Throwable t) {
            throw new NotesException("getParents of " + document, t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public StandardFolder deleteFolder(long folderId) throws NotesException {
        try {

            return _delete(_get(folderId));

        } catch (NotesException e) {
            throw e;
        } catch (Throwable t) {
            throw new NotesException("delete folder " + folderId, t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public StandardFolder updateFolder(long folderId, Folder newFolder) throws NotesException {
        try {
            if (newFolder == null) {
                throw new NotesException("Folder is null");
            }
            /*
            todo
            check if parentId changed -> ..
              */

            StandardFolder folder = _get(folderId);
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

    private StandardFolder _get(Long folderId) throws NotesException {

        if (folderId == null || folderId <= 0) {
            throw new NotesException(String.format("Invalid folder id '%s'", folderId));
        }

        Query query = em.createNamedQuery(StandardFolder.QUERY_BY_ID);
        query.setParameter("ID", folderId);

        List<StandardFolder> folderList = query.getResultList();
        if (folderList.isEmpty()) {
            throw new NotesException(String.format("No folder with id '%s' found", folderId));
        }

        return folderList.get(0);

    }

    private Folder _create(Folder folder, Folder parent, Database database) throws NotesException {

        if (folder == null) {
            throw new NotesException("Folder is null");
        }

        if (parent != null) {

            if (parent.getLevel() >= 5) {
                throw new NotesException("Max folder depth reached.");
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

        User user = (User) _getProxy(User.class, database.getOwner());
        user.getFolders().add(folder);
        em.merge(user);

        return folder;

    }

    private StandardFolder _delete(StandardFolder folder) throws NotesException {
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
