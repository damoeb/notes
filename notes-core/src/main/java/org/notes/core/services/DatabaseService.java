package org.notes.core.services;

import org.notes.common.exceptions.NotesException;
import org.notes.core.domain.StandardDatabase;
import org.notes.core.domain.StandardFolder;
import org.notes.core.domain.User;

import javax.ejb.Local;
import java.util.List;

@Local
public interface DatabaseService {

    StandardDatabase createDatabase(StandardDatabase database, User user) throws NotesException;

    StandardDatabase getDatabase(long databaseId) throws NotesException;

    StandardDatabase deleteDatabase(long databaseId) throws NotesException;

    void setTrashFolder(StandardDatabase database, StandardFolder folder) throws NotesException;

    StandardDatabase updateDatabase(long databaseId, StandardDatabase database) throws NotesException;

    StandardDatabase getDatabaseOfUser() throws NotesException;

    List<StandardFolder> getRootFolders(long databaseId) throws NotesException;

    void setDefaultFolder(StandardDatabase database, StandardFolder folder) throws NotesException;
}
