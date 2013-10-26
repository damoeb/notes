package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.core.model.Account;
import org.notes.core.model.User;

import javax.ejb.Local;

@Local
public interface UserManager {

    User getUser(Long userId) throws NotesException;

    User deleteUser(Long userId) throws NotesException;

    User createUser(User user, Account account) throws NotesException;

}
