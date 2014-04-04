package org.notes.core.services;

import org.notes.common.exceptions.NotesException;
import org.notes.core.domain.Account;
import org.notes.core.domain.AccountType;

import javax.ejb.Local;

@Local
public interface AccountService {

    Account getAccount(AccountType type) throws NotesException;

    Account createAccount(Account account) throws NotesException;
}
