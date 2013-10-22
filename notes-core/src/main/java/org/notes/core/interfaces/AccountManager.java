package org.notes.core.interfaces;

import org.notes.core.model.Account;
import org.notes.core.model.User;

import javax.ejb.Local;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

@Local
public interface AccountManager {

    Account getAccount(Long accountId);

    Account createAccount(String name, long quota);
}
