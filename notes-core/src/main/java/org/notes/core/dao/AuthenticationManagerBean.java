package org.notes.core.dao;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.core.interfaces.AuthenticationManager;
import org.notes.core.interfaces.UserManager;
import org.notes.core.model.User;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
    public User authenticate(String username, String password) throws NotesException {
        try {

            if (StringUtils.isBlank(username)) {
                throw new NotesException(String.format("Invalid username '%s'", username));
            }

            // todo implement
            /*
            Query query = em.createNamedQuery(User.QUERY_BY_ID);
            query.setParameter("USERNAME", username);

            List<User> userList = query.getResultList();
            if (userList.isEmpty()) {
                throw new NotesException(String.format("No user '%s' found", username));
            }

            return userList.get(0);
            */
            return null;

        } catch (NotesException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesException(String.format("authenticate %s", username), t);
        }
    }
}
