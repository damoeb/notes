package org.notes.core.dao;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.core.interfaces.AccountManager;
import org.notes.core.interfaces.UserManager;
import org.notes.core.model.Account;
import org.notes.core.model.User;

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
public class UserManagerBean implements UserManager {

    private static final Logger LOGGER = Logger.getLogger(UserManagerBean.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private AccountManager accountManager;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public User getUser(String username) throws NotesException {
        try {

            if (StringUtils.isBlank(username)) {
                throw new NotesException(String.format("Invalid username '%s'", username));
            }

            Query query = em.createNamedQuery(User.QUERY_BY_ID);
            query.setParameter("USERNAME", username);

            List<User> userList = query.getResultList();
            if (userList.isEmpty()) {
                throw new NotesException(String.format("No user '%s' found", username));
            }

            return userList.get(0);

        } catch (NotesException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesException(String.format("get user %s", username), t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public User deleteUser(String username) throws NotesException {
        try {

            User user = getUser(username);
            em.remove(user);

            return user;

        } catch (NotesException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesException(String.format("get user %s", username), t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public User createUser(User user, Account account) throws NotesException {
        try {

            if (user == null) {
                throw new NotesException("user is null");
            }
            if (account == null) {
                throw new NotesException("account is null");
            }

            em.persist(user);
            em.flush();
            em.refresh(user);

            account.getUsers().add(user);
            em.merge(account);

            return user;

        } catch (NotesException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesException("create user", t);
        }
    }
}
