package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.core.model.User;

import javax.ejb.Local;

@Local
public interface AuthenticationManager {

    User authenticate(String username, String password) throws NotesException;
}
