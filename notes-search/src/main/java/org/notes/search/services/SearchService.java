package org.notes.search.services;

import org.notes.common.domain.Document;
import org.notes.common.exceptions.NotesException;
import org.notes.search.domain.SearchResponse;

import javax.ejb.Local;

@Local
public interface SearchService {

    /**
     * Execute search
     *
     * @param queryString     the lucene query
     * @param start           the offset in the resultset
     * @param rows            the max number of results
     * @param databaseId
     * @param currentFolderId id of folder, current position
     * @param contextOnly     true, if search is performed only in <code>currentFolderId</code>
     * @return
     * @throws NotesException
     */
    SearchResponse query(String queryString, Integer start, Integer rows, Long databaseId, Integer currentFolderId, Boolean contextOnly) throws NotesException;

    /**
     * Have document indexed
     *
     * @param document
     * @throws NotesException
     */
    void index(Document document) throws NotesException;

}
