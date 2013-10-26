package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.core.model.Account;

import javax.ejb.Local;

@Local
public interface AccountManager {

    Account getAccount(Long accountId) throws NotesException;

    Account createAccount(Account account) throws NotesException;
}
