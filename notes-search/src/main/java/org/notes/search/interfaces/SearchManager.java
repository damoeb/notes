package org.notes.search.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.search.dao.SearchResponse;

import javax.ejb.Local;

@Local
public interface SearchManager {

    /**
     * Execute search
     * @param queryString the lucene query
     * @param start the offset in the resultset
     * @param rows the max number of results
     * @param databaseId
     * @param currentFolderId id of folder, current position
     * @param contextOnly true, if search is performed only in <code>currentFolderId</code>
     * @return
     * @throws NotesException
     */
    SearchResponse query(String queryString, Integer start, Integer rows, Long databaseId, Integer currentFolderId, Boolean contextOnly) throws NotesException;

}
