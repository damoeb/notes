package org.notes.core.services;

import org.notes.common.exceptions.NotesException;
import org.notes.core.domain.Account;
import org.notes.core.domain.User;

import javax.ejb.Local;

@Local
public interface UserService {

    User getUser(String username) throws NotesException;

    User deleteUser(String username) throws NotesException;

    User createUser(User user, Account account) throws NotesException;

}
