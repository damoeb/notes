package org.notes.core.dao;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.interfaces.AccountManager;
import org.notes.core.model.Account;
import org.notes.core.model.Folder;
import org.notes.core.request.NotesRequestException;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.core.Response;
import java.util.List;

//@LocalBean
@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class AccountManagerBean implements AccountManager {

    private static final Logger LOGGER = Logger.getLogger(AccountManagerBean.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Account getAccount(Long accountId) {
        try {

            if(accountId==null||accountId<=0) {
                throw new NotesRequestException(Response.Status.BAD_REQUEST, String.format("Invalid account id '%s'", accountId));
            }

            Query query = em.createNamedQuery(Account.QUERY_BY_ID);
            query.setParameter("ID", accountId);

            List<Account> accountList = query.getResultList();
            if(accountList.isEmpty()) {
                throw new NotesRequestException(Response.Status.NOT_FOUND, String.format("No account with id '%s' found", accountId));
            }

            Account account = accountList.get(0);

            //em.detach(account);

            return account;

        } catch (NotesRequestException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesRequestException("get account by id", t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Account createAccount(String name, long quota) {
        try {

            if(StringUtils.isBlank(name)) {
                throw new NotesRequestException(Response.Status.BAD_REQUEST, String.format("Invalid account name '%s'", name));
            }

            if(quota < 0) {
                throw new NotesRequestException(Response.Status.BAD_REQUEST, String.format("Invalid quota '%s'", quota));
            }

            Account account = new Account();
            account.setName(name);
            account.setQuota(quota);

            em.persist(account);
            em.flush();
            em.refresh(account);

            return account;

        } catch (NotesRequestException t) {
            throw t;
        } catch (Throwable t) {
            throw new NotesRequestException("get account by id", t);
        }
    }
}
