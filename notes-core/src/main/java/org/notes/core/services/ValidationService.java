package org.notes.core.services;

import org.notes.common.exceptions.NotesException;

import javax.ejb.Local;

@Local
public interface ValidationService {

    void tryUsername(String username) throws NotesException;

    void tryPassword(String password) throws NotesException;

    void tryEmail(String email) throws NotesException;
}
