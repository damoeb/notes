package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.core.model.Database;

import javax.ejb.Local;
import java.util.List;

@Local
public interface DatabaseManager {

    Database createDatabase(Database database) throws NotesException;

    Database getDatabase(long databaseId) throws NotesException;

    Database deleteDatabase(long databaseId) throws NotesException;

    Database updateDatabase(long databaseId, Database folder) throws NotesException;

    List<Database> getDatabases() throws NotesException;

}
