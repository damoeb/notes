package org.notes.core.dao;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.interfaces.FolderManager;
import org.notes.core.interfaces.UserManager;
import org.notes.core.model.Folder;
import org.notes.core.model.User;
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
    public Folder createNotebook(Long userId, String name) {
        try {
            Folder folder = _createFolder(userId, name);
            User user = userManager.getUser(userId);
            //user.getNotebooks().add(folder);
            em.merge(user);

            return folder;

        } catch (NotesRequestException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesRequestException("create notebook", t);
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

            //Hibernate.initialize(note.getMetricResults());
            //em.detach(user);

            return folderList.get(0);

        } catch (NotesRequestException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesRequestException("get user by id", t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Folder createFolder(Long parentId, Long userId, String name) {
        try {

            if (parentId == null || parentId <= 0) {
                throw new NotesRequestException(Response.Status.BAD_REQUEST, String.format("Invalid parent id '%s'", parentId));
            }
            Folder parent = getById(parentId);

            Folder folder = _createFolder(userId, name);
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

    private Folder _createFolder(Long userId, String name) {

        if (StringUtils.isBlank(name)) {
            throw new NotesRequestException(Response.Status.BAD_REQUEST, String.format("Invalid name '%s'", name));
        }

        User user = userManager.getUser(userId);

        Folder folder = new Folder();
        folder.setName(name);
        // todo folder.setOwner(user);

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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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


//    @Override
//    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
//    public Note getById(long noteId) {
//        try {
//            Query query = em.createNamedQuery(Note.QUERY_BY_ID);
//            query.setParameter("ID", noteId);
//            Note note = (Note) query.getSingleResult();
//            //Hibernate.initialize(note.getMetricResults());
//            //em.detach(note);
//
//            return note;
//
//        } catch (NotesRequestException t) {
//            throw t;
//        } catch (Throwable t) {
//            throw new NotesRequestException("get note by id", t);
//        }
//    }

}
