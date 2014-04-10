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
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import java.util.List;

//@LocalBean
@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = Logger.getLogger(UserServiceImpl.class);

    @PersistenceUnit(unitName = "primary")
    private EntityManagerFactory emf;

    @Inject
    private AccountService accountService;

    @Inject
    private DatabaseService databaseService;

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public User getUser(String username) throws NotesException {
        EntityManager em = null;

        try {
            em = emf.createEntityManager();

            if (StringUtils.isBlank(username)) {
                throw new IllegalArgumentException(String.format("Invalid username '%s'", username));
            }

            Query query = em.createNamedQuery(User.QUERY_BY_ID);
            query.setParameter("USERNAME", username);

            List<User> userList = query.getResultList();
            if (userList.isEmpty()) {
                throw new IllegalArgumentException(String.format("No user '%s' found", username));
            }

            return userList.get(0);

        } catch (Throwable t) {
            String message = String.format("Cannot run getUser, username=%s. Reason: %s", username, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public User deleteUser(String username) throws NotesException {
        EntityManager em = null;

        try {
            em = emf.createEntityManager();

            User user = getUser(username);
            em.remove(user);

            return user;

        } catch (Throwable t) {
            String message = String.format("Cannot run deleteUser, username=%s. Reason: %s", username, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public User createUser(User newUser, Account account) throws NotesException {
        EntityManager em = null;

        try {
            em = emf.createEntityManager();

            if (newUser == null) {
                throw new IllegalArgumentException("user is null");
            }

            if (account == null) {
                throw new IllegalArgumentException("account is null");
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

        } catch (Throwable t) {
            String message = String.format("Cannot run createUser, newUser=%s, account=%s. Reason: %s", newUser, account, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);

        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
