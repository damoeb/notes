package org.notes.core.dao;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.interfaces.FolderManager;
import org.notes.core.interfaces.UserManager;
import org.notes.core.model.Folder;
import org.notes.core.request.NotesRequestException;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.core.Response;
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

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Folder createDatabase(String name) {
        try {
            return _createFolder(name);

        } catch (NotesRequestException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesRequestException("create database", t);
        }
    }

    @Override
    public List<Folder> getDatabases() {
        try {
            Query query = em.createNamedQuery(Folder.QUERY_USERS_NOTEBOOKS);
            query.setParameter("ID", 1);  // todo userId

            return query.getResultList();

        } catch (NotesRequestException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesRequestException("get databases", t);
        }
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Folder getById(Long folderId) {
        try {

            if (folderId == null || folderId <= 0) {
                throw new NotesRequestException(Response.Status.BAD_REQUEST, String.format("Invalid folder id '%s'", folderId));
            }

            Query query = em.createNamedQuery(Folder.QUERY_BY_ID);
            query.setParameter("ID", folderId);

            List<Folder> folderList = query.getResultList();
            if (folderList.isEmpty()) {
                throw new NotesRequestException(Response.Status.NOT_FOUND, String.format("No folder with id '%s' found", folderId));
            }

            return folderList.get(0);

        } catch (NotesRequestException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesRequestException("get user by id", t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Folder createFolder(Long parentId, String name) {
        try {

            if (parentId == null || parentId <= 0) {
                throw new NotesRequestException(Response.Status.BAD_REQUEST, String.format("Invalid parent id '%s'", parentId));
            }
            Folder parent = getById(parentId);

            Folder folder = _createFolder(name);
            folder.setParent(parent);
            em.merge(folder);
            em.flush();
            em.refresh(folder);

            em.merge(parent);

            return folder;

        } catch (NotesRequestException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesRequestException("get user by id", t);
        }
    }

    private Folder _createFolder(String name) {

        if (StringUtils.isBlank(name)) {
            throw new NotesRequestException(Response.Status.BAD_REQUEST, String.format("Invalid name '%s'", name));
        }

        Folder folder = new Folder();
        folder.setName(name);
        folder.setOwnerId(1l); // todo userId

        em.persist(folder);
        em.flush();
        em.refresh(folder);

        return folder;

    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Folder renameFolder(Long folderId, String name) {
        try {

            if (folderId == null || folderId <= 0) {
                throw new NotesRequestException(Response.Status.BAD_REQUEST, String.format("Invalid id '%s'", folderId));
            }
            if (StringUtils.isBlank(name)) {
                throw new NotesRequestException(Response.Status.BAD_REQUEST, String.format("Invalid name '%s'", folderId));
            }
            Folder folder = getById(folderId);
            folder.setName(name);
            em.merge(folder);

            return folder;

        } catch (NotesRequestException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesRequestException("rename Folder", t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Folder removeFolder(Long folderId) {
        try {

            //getById(folderId)
            //move all descendant notes toString() its parent;
            return null;

        } catch (NotesRequestException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesRequestException("get user by id", t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<Folder> getChildren(Long folderId) {
        try {

            if (folderId == null || folderId <= 0) {
                throw new NotesRequestException(Response.Status.BAD_REQUEST, String.format("Invalid id '%s'", folderId));
            }

            Query query = em.createNamedQuery(Folder.QUERY_GET_CHILDREN);
            query.setParameter("PARENT_ID", folderId);

            return query.getResultList();

        } catch (NotesRequestException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesRequestException("get children", t);
        }
    }

    @Override
    public Folder moveFolder(Long folderId, Long newParentId) {
        // todo implement
        return null;
    }

    @Override
    public Folder moveNote(Long noteId, Long newParentId) {
        // todo implement
        return null;
    }

}
