package org.notes.core.services;

import org.notes.common.domain.Document;
import org.notes.common.exceptions.NotesException;
import org.notes.core.domain.SearchResponse;

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
     * @return
     * @throws NotesException
     */
    SearchResponse query(String queryString, Integer start, Integer rows, Long databaseId, Integer currentFolderId) throws NotesException;

    /**
     * Have document indexed
     *
     * @param document
     * @throws NotesException
     */
    void index(Document document) throws NotesException;

}
