package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.core.model.UserSettings;

import javax.ejb.Local;

@Local
public interface AuthenticationManager {

    UserSettings authenticate(String username, String password) throws NotesException;
}
