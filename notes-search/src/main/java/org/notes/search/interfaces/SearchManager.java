package org.notes.search.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.search.dao.SearchResponse;

import javax.ejb.Local;

@Local
public interface SearchManager {

    SearchResponse query(long databaseId, String queryString, int start, int rows) throws NotesException;

}
