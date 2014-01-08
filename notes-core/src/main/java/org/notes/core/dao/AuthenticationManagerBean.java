package org.notes.core.dao;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.core.interfaces.AuthenticationManager;
import org.notes.core.interfaces.UserManager;
import org.notes.core.model.User;
import org.notes.core.model.UserSettings;
import org.notes.core.util.PasswordHash;

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
public class AuthenticationManagerBean implements AuthenticationManager {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationManagerBean.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private UserManager userManager;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public UserSettings authenticate(String username, String password) throws NotesException {
        try {

            NotesException ex = new NotesException("User or password is invalid");

            if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
                throw ex;
            }

            Query query = em.createNamedQuery(User.QUERY_BY_ID);
            query.setParameter("USERNAME", username);

            List<User> userList = query.getResultList();
            if (userList.isEmpty()) {
                throw ex;
            }

            User user = userList.get(0);

            boolean authorized = PasswordHash.validatePassword(password, user.getPasswordHash());

            if (!authorized) {
                throw ex;
            }

            UserSettings settings = new UserSettings();

            settings.setUser(user);
            // todo load settings

            return null;

        } catch (NotesException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesException(String.format("authenticate %s", username), t);
        }
    }
}
