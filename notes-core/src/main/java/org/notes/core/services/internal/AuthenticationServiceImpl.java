package org.notes.core.services.internal;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.common.services.FolderService;
import org.notes.core.domain.*;
import org.notes.core.services.AccountService;
import org.notes.core.services.AuthenticationService;
import org.notes.core.services.DatabaseService;
import org.notes.core.services.UserService;
import org.notes.core.util.PasswordHash;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

//@LocalBean
@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationServiceImpl.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private DatabaseService databaseService;

    @Inject
    private AccountService accountService;

    @Inject
    private UserService userService;

    @Inject
    private FolderService folderService;

    @Inject
    private SessionData sessionData;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SessionData authenticate(String username, String password) throws NotesException {
        try {

            NotesException ex = new NotesException("User or password is invalid");

            if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
                throw ex;
            }

            Query query = em.createNamedQuery(User.QUERY_BY_ID);
            query.setParameter("USERNAME", username);

            User user = (User) query.getSingleResult();

            boolean authorized = PasswordHash.validatePassword(password, user.getPasswordHash());

            if (!authorized) {
                throw ex;
            }

            sessionData.setUser(user);
            Hibernate.initialize(user.getDatabases());
            sessionData.setDatabases(user.getDatabases());

            return sessionData;

        } catch (NotesException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesException(String.format("authenticate %s", username), t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public User register(String username, String password, String email) throws NotesException {
        try {

            if (StringUtils.isBlank(username)) {
                throw new NotesException("username is null");
            }
            if (StringUtils.isBlank(password)) {
                throw new NotesException("password is too short");
            }
            if (StringUtils.isBlank(email)) {
                throw new NotesException("email is null");
            }

            User user = new User();
            user.setUsername(username);
            user.setPasswordHash(PasswordHash.createHash(password));
            user.setEmail(email);

            Account account = accountService.getAccount(AccountType.BASIC);

            user = userService.createUser(user, account);

            StandardDatabase database = databaseService.createDatabase(new StandardDatabase(), user);

            StandardFolder unsorted = new StandardFolder();
            unsorted.setName("Unsorted");
            folderService.createFolder(unsorted, null, database);

            databaseService.setDefaultFolder(database, unsorted);

            StandardFolder trash = new StandardFolder();
            trash.setName("Trash");
            folderService.createFolder(trash, null, database);

            databaseService.setTrashFolder(database, trash);

            return user;
        } catch (NotesException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesException("register user", t);
        }
    }
}
