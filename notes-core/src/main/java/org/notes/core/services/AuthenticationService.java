package org.notes.core.services;

import org.notes.common.exceptions.NotesException;
import org.notes.core.domain.NotesSession;
import org.notes.core.domain.User;

import javax.ejb.Local;

@Local
public interface AuthenticationService {

    NotesSession authenticate(String username, String password) throws NotesException;

    User register(String username, String password, String email) throws NotesException;
}
