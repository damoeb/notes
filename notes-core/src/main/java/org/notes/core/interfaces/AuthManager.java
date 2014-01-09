package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.core.model.User;

import javax.ejb.Local;

@Local
public interface AuthManager {

    SessionData authenticate(String username, String password) throws NotesException;

    User register(String username, String password, String email) throws NotesException;
}
