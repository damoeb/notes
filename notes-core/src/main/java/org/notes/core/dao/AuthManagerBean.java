package org.notes.core.dao;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.common.interfaces.FolderManager;
import org.notes.core.interfaces.*;
import org.notes.core.model.*;
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
public class AuthManagerBean implements AuthManager {

    private static final Logger LOGGER = Logger.getLogger(AuthManagerBean.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private DatabaseManager databaseManager;

    @Inject
    private AccountManager accountManager;

    @Inject
    private UserManager userManager;

    @Inject
    private FolderManager folderManager;

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

            Account account = accountManager.getAccount(AccountType.BASIC);

            user = userManager.createUser(user, account);

            StandardDatabase database = databaseManager.createDatabase(new StandardDatabase(), user);

            StandardFolder unsorted = new StandardFolder();
            unsorted.setName("Unsorted");
            folderManager.createFolder(unsorted, null, database);

            databaseManager.setDefaultFolder(database, unsorted);

            StandardFolder trash = new StandardFolder();
            trash.setName("Trash");
            folderManager.createFolder(trash, null, database);

            databaseManager.setTrashFolder(database, trash);

            return user;
        } catch (NotesException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesException("register user", t);
        }
    }
}
