package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.core.model.Account;
import org.notes.core.model.User;

import javax.ejb.Local;

@Local
public interface UserManager {

    User getUser(String username) throws NotesException;

    User deleteUser(String username) throws NotesException;

    User createUser(User user, Account account) throws NotesException;

}
