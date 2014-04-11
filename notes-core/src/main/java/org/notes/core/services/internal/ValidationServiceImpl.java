package org.notes.core.services.internal;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.log4j.Logger;
import org.notes.common.configuration.Configuration;
import org.notes.common.configuration.ConfigurationProperty;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.core.domain.User;
import org.notes.core.services.ValidationService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

//@LocalBean
@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class ValidationServiceImpl implements ValidationService {

    private static final Logger LOGGER = Logger.getLogger(ValidationServiceImpl.class);

    @ConfigurationProperty(value = Configuration.CONSTRAINT_PWD_MIN_LEN, mandatory = true)
    private int pwdMinLength;

    @ConfigurationProperty(value = Configuration.CONSTRAINT_PWD_MAX_LEN, mandatory = true)
    private int pwdMaxLength;

    @ConfigurationProperty(value = Configuration.CONSTRAINT_USERNAME_MIN_LEN, mandatory = true)
    private int usrMinLength;

    @ConfigurationProperty(value = Configuration.CONSTRAINT_USERNAME_MAX_LEN, mandatory = true)
    private int usrMaxLength;

    // --

    @PersistenceUnit(unitName = "primary")
    private EntityManagerFactory emf;

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void tryUsername(String username) throws NotesException {

        EntityManager em = null;

        try {
            if (StringUtils.isEmpty(username)) {
                throw new IllegalArgumentException("parameter is null");
            }
            if (username.length() < usrMinLength) {
                throw new IllegalArgumentException("too short");
            }
            if (username.length() > usrMaxLength) {
                throw new IllegalArgumentException("too long");
            }

            em = emf.createEntityManager();

            // check if username is not yet taken
            Query query = em.createNamedQuery(User.QUERY_BY_ID);
            query.setParameter("USERNAME", username);
            if (!query.getResultList().isEmpty()) {
                throw new IllegalArgumentException("already taken");
            }

        } catch (Throwable t) {
            String message = String.format("Cannot validate username %s. Reason: %s", username, t.getMessage());
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
    public void tryPassword(String password) throws NotesException {
        try {
            if (StringUtils.isEmpty(password)) {
                throw new IllegalArgumentException("parameter is null");
            }
            if (password.length() < pwdMinLength) {
                throw new IllegalArgumentException("too short");
            }
            if (password.length() > pwdMaxLength) {
                throw new IllegalArgumentException("too long");
            }

        } catch (Throwable t) {
            String message = String.format("Cannot validate password. Reason: %s", t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tryEmail(String email) throws NotesException {
        try {
            if (StringUtils.isEmpty(email)) {
                throw new IllegalArgumentException("parameter is null");
            }
            if (!EmailValidator.getInstance(true).isValid(email)) {
                throw new IllegalArgumentException("invalid");
            }

        } catch (Throwable t) {
            String message = String.format("Cannot validate email %s. Reason: %s", email, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        }
    }

}
