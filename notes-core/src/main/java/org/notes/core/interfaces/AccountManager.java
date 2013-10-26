package org.notes.core.interfaces;

import org.notes.core.model.Account;

import javax.ejb.Local;

@Local
public interface AccountManager {

    Account getAccount(Long accountId);

    Account createAccount(String name, long quota);
}
