package org.notes.core.services;

import org.notes.common.exceptions.NotesException;
import org.notes.core.domain.Account;
import org.notes.core.domain.AccountType;

import javax.ejb.Local;

@Local
public interface AccountService {

    Account getByType(AccountType type) throws NotesException;

    Account getById(int accountId) throws NotesException;

    Account createAccount(Account account) throws NotesException;
}
