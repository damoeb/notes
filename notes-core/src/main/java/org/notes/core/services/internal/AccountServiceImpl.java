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
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

//@LocalBean
@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class AccountServiceImpl implements AccountService {

    private static final Logger LOGGER = Logger.getLogger(AccountServiceImpl.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Account getAccount(AccountType type) throws NotesException {
        try {

            if (type == null) {
                throw new NotesException(String.format("Invalid account type"));
            }

            Query query = em.createNamedQuery(Account.QUERY_BY_TYPE);
            query.setParameter("TYPE", type);

            List<Account> accountList = query.getResultList();
            if (accountList.isEmpty()) {
                throw new NotesException(String.format("No account with type '%s' found", type));
            }

            return accountList.get(0);

        } catch (NotesException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesException("get account by id", t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Account createAccount(Account account) throws NotesException {
        try {

            if (account == null) {
                throw new NotesException("account is null");
            }

            em.persist(account);
            em.flush();
            em.refresh(account);

            return account;

        } catch (NotesException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesException("create account", t);
        }
    }
}