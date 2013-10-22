package org.notes.core.dao;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.interfaces.AccountManager;
import org.notes.core.interfaces.FolderManager;
import org.notes.core.interfaces.UserManager;
import org.notes.core.model.Account;
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
public class UserManagerBean implements UserManager {

    private static final Logger LOGGER = Logger.getLogger(UserManagerBean.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private AccountManager accountManager;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public User getUser(Long userId) {
        try {

            if(userId==null||userId<=0) {
                throw new NotesRequestException(Response.Status.BAD_REQUEST, String.format("Invalid user id '%s'", userId));
            }

            Query query = em.createNamedQuery(User.QUERY_BY_ID);
            query.setParameter("ID", userId);

            List<User> userList = query.getResultList();
            if(userList.isEmpty()) {
                throw new NotesRequestException(Response.Status.NOT_FOUND, String.format("No user with id '%s' found", userId));
            }

            User user = userList.get(0);
            //Hibernate.initialize(user.getRoot());
            em.detach(user);
            //user.setNotebooks(null);
            //user.setRoot(null);

            return user;

        } catch (NotesRequestException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesRequestException("get user by id", t);
        }
    }

    @Override
    public long getUserId() {
        return 1;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public User createUser(String name, Account account) {
        try {

            if(StringUtils.isBlank(name)) {
                throw new NotesRequestException(Response.Status.BAD_REQUEST, String.format("Invalid user name '%s'", name));
            }

            User user = new User();
            user.setUsername(name);
            user.setAccount(account);

            em.persist(user);
            em.flush();
            em.refresh(user);

            return user;

        } catch (NotesRequestException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesRequestException("get user by id", t);
        }
    }
}
