package org.notes.core.services.internal;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.core.domain.Account;
import org.notes.core.domain.User;
import org.notes.core.services.AccountService;
import org.notes.core.services.DatabaseService;
import org.notes.core.services.UserService;

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
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = Logger.getLogger(UserServiceImpl.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private AccountService accountService;

    @Inject
    private DatabaseService databaseService;

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
    public User createUser(User newUser, Account account) throws NotesException {
        try {

            if (newUser == null) {
                throw new NotesException("user is null");
            }

            if (account == null) {
                throw new NotesException("account is null");
            }

            if (!em.contains(account)) {
                account = accountService.getAccount(account.getType());
            }

            em.persist(newUser);
            em.flush();
            em.refresh(newUser);

            account.getUsers().add(newUser);
            em.merge(account);

            return newUser;

        } catch (NotesException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesException("create user", t);
        }
    }
}
