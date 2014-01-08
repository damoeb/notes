package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.core.model.Account;
import org.notes.core.model.User;

import javax.ejb.Local;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

@Local
public interface UserManager {

    User getUser(String username) throws NotesException;

    User deleteUser(String username) throws NotesException;

    User createUser(User user, Account account) throws NotesException;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    User registerUser(String username, String password, String email) throws NotesException;
}
