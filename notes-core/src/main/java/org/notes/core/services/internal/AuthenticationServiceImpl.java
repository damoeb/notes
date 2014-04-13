package org.notes.core.services.internal;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.common.services.FolderService;
import org.notes.core.domain.*;
import org.notes.core.services.*;
import org.notes.core.util.CryptUtils;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import java.security.NoSuchAlgorithmException;
import java.util.List;

//@LocalBean
@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationServiceImpl.class);

    private static final int MAX_LOGIN_TRIES = 3;

    @PersistenceUnit(unitName = "primary")
    private EntityManagerFactory emf;

    @Inject
    private DatabaseService databaseService;

    @Inject
    private AccountService accountService;

    @Inject
    private UserService userService;

    @Inject
    private FolderService folderService;

    @Inject
    private ValidationService validationService;

    @Inject
    private NotesSession notesSession;

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public NotesSession authenticate(String username, String password) throws NotesException {

        EntityManager em = null;

        try {

            if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
                throw new IllegalArgumentException("User or password is empty");
            }

            em = emf.createEntityManager();

            IllegalArgumentException exception = new IllegalArgumentException("User, password combination is invalid or unknown");

            Query query = em.createNamedQuery(User.QUERY_BY_ID);
            query.setParameter("USERNAME", username);

            List users = query.getResultList();
            if (users.isEmpty()) {
                throw exception;
            }

            User user = (User) users.get(0);
            if (user.isDeactivated()) {
                throw exception;
            }

            user.setLoginTries(user.getLoginTries() + 1);

            if (user.getLoginTries() > MAX_LOGIN_TRIES) {
                user.setDeactivated(true);
                em.merge(user);

                throw exception;
            }
            if (!isEqualPassword(password, user)) {
                em.merge(user);

                throw exception;
            }

            user.setLoginTries(0);
            em.merge(user);

            notesSession.setUser(user);
            Hibernate.initialize(user.getDatabases());
            notesSession.setDatabases(user.getDatabases());

            return notesSession;

        } catch (Throwable t) {
            String message = String.format("Cannot run authenticate, user=%s. Reason: %s", username, t.getMessage());
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
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public User register(String username, String password, String email) throws NotesException {
        try {

            if (StringUtils.isBlank(username)) {
                throw new IllegalArgumentException("username is null");
            }
            if (StringUtils.isBlank(password)) {
                throw new IllegalArgumentException("password is too short");
            }
            if (StringUtils.isBlank(email)) {
                throw new IllegalArgumentException("email is null");
            }

            validationService.tryUsername(username);
            validationService.tryPassword(username);
            validationService.tryEmail(email);

            User user = new User();
            user.setUsername(username);

            String salt = String.valueOf((username + email).hashCode());
            user.setSalt(salt);
            user.setPasswordHash(CryptUtils.hash(password, salt));
            user.setEmail(email);

            Account account = accountService.getByType(AccountType.ALPHA);

            user = userService.createUser(user, account);

            StandardDatabase database = databaseService.createDatabase(new StandardDatabase(), user);

            StandardFolder unsorted = new StandardFolder("Unsorted");
            folderService.createFolder(unsorted, null, database);

            databaseService.setDefaultFolder(database, unsorted);

            StandardFolder trash = new StandardFolder("Trash");
            folderService.createFolder(trash, null, database);


            // see http://sourcesofinsight.com/build-a-personal-knowledge-base-of-success-stories-insight-and-action-to-improve-your-success/

            folderService.createFolder(new StandardFolder("Patterns"), null, database);
            folderService.createFolder(new StandardFolder("People"), null, database);
            folderService.createFolder(new StandardFolder("Principles"), null, database);
            folderService.createFolder(new StandardFolder("Questions"), null, database);
            folderService.createFolder(new StandardFolder("Techniques"), null, database);
            folderService.createFolder(new StandardFolder("To-do"), null, database);
            folderService.createFolder(new StandardFolder("Ideas"), null, database);
            folderService.createFolder(new StandardFolder("Tasks"), null, database);

            databaseService.setTrashFolder(database, trash);

            return user;

        } catch (Throwable t) {
            String message = String.format("Cannot run register, username=%s, email=%s. Reason: %s", username, email, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);

        }
    }

    // --

    private boolean isEqualPassword(String password, User user) throws NoSuchAlgorithmException {
        return StringUtils.equals(user.getPasswordHash(), CryptUtils.hash(password, user.getSalt()));
    }
}
