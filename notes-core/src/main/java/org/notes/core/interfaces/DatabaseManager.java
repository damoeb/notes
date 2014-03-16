package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.core.model.StandardDatabase;
import org.notes.core.model.StandardFolder;
import org.notes.core.model.User;

import javax.ejb.Local;
import java.util.List;

@Local
public interface DatabaseManager {

    StandardDatabase createDatabase(StandardDatabase database, User user) throws NotesException;

    StandardDatabase getDatabase(long databaseId) throws NotesException;

    StandardDatabase deleteDatabase(long databaseId) throws NotesException;

    StandardDatabase updateDatabase(long databaseId, StandardDatabase database) throws NotesException;

    StandardDatabase getDatabaseOfUser() throws NotesException;

    List<StandardFolder> getFolders(long databaseId) throws NotesException;

    void setDefaultFolder(StandardDatabase database, StandardFolder folder) throws NotesException;
}
