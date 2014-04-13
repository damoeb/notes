package org.notes.core.services.internal;

import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.core.domain.Account;
import org.notes.core.domain.AccountType;
import org.notes.core.services.AccountService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import java.util.List;

//@LocalBean
@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class AccountServiceImpl implements AccountService {

    private static final Logger LOGGER = Logger.getLogger(AccountServiceImpl.class);

    @PersistenceUnit(unitName = "primary")
    private EntityManagerFactory emf;


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Account getByType(AccountType type) throws NotesException {
        EntityManager em = null;

        try {
            if (type == null) {
                throw new IllegalArgumentException(String.format("Invalid account type"));
            }

            em = emf.createEntityManager();

            Query query = em.createNamedQuery(Account.QUERY_BY_TYPE);
            query.setParameter("TYPE", type);

            List<Account> accountList = query.getResultList();
            if (accountList.isEmpty()) {
                throw new NotesException(String.format("No account with type '%s' found", type));
            }

            return accountList.get(0);

        } catch (Throwable t) {
            String message = String.format("Cannot run getByType, type=%s. Reason: %s", type, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Account getById(int accountId) throws NotesException {
        EntityManager em = null;

        try {

            em = emf.createEntityManager();
            return em.find(Account.class, accountId);

        } catch (Throwable t) {
            String message = String.format("Cannot run getById, id=%s. Reason: %s", accountId, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Account createAccount(Account account) throws NotesException {
        EntityManager em = null;

        try {

            if (account == null) {
                throw new IllegalArgumentException("account is null");
            }

            em = emf.createEntityManager();
            em.persist(account);
            em.flush();
            em.refresh(account);

            return account;

        } catch (Throwable t) {
            String message = String.format("Cannot run createAccount, account=%s. Reason: %s", account, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);

        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
