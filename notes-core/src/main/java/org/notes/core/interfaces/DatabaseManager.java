package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.core.model.Database;
import org.notes.core.model.Folder;
import org.notes.core.model.User;

import javax.ejb.Local;
import java.util.List;

@Local
public interface DatabaseManager {

    Database createDatabase(Database database, User user) throws NotesException;

    Database getDatabase(long databaseId) throws NotesException;

    Database deleteDatabase(long databaseId) throws NotesException;

    Database updateDatabase(long databaseId, Database database) throws NotesException;

    List<Database> getDatabasesOfCurrentUser() throws NotesException;

    List<Folder> getFolders(long databaseId) throws NotesException;

}
