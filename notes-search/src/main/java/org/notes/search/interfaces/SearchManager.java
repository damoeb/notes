package org.notes.search.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.search.model.DocumentHit;

import javax.ejb.Local;
import java.util.List;

@Local
public interface SearchManager {

    List<DocumentHit> query(long databaseId, String queryString, int start, int rows) throws NotesException;

}
