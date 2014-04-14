package org.notes.core.services;

import org.notes.common.domain.Document;
import org.notes.common.exceptions.NotesException;
import org.notes.core.domain.SearchQuery;
import org.notes.core.domain.SearchResponse;

import javax.ejb.Asynchronous;
import javax.ejb.Local;
import java.util.Collection;
import java.util.List;

@Local
public interface SearchService {

    /**
     * User History of executed queries
     *
     * @throws org.notes.common.exceptions.NotesException
     */
    List<SearchQuery> getLastQueries() throws NotesException;

    @Asynchronous
    void asyncQueryLogging(String query) throws NotesException;

    /**
     * Execute search
     *
     * @param queryString     the lucene find
     * @param start           the offset in the resultset
     * @param rows            the max number of results
     * @param databaseId
     * @param currentFolderId id of folder, current position
     * @return
     * @throws NotesException
     */
    SearchResponse find(String queryString, Integer start, Integer rows, Long databaseId, Integer currentFolderId) throws NotesException;

    /**
     * Have document indexed
     *
     * @param documents
     * @throws NotesException
     */
    void index(Collection<Document> documents) throws NotesException;


    List<SearchQuery> getQuerySuggestions(String query) throws NotesException;
}
