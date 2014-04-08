package org.notes.core.services;

import org.notes.common.exceptions.NotesException;
import org.notes.core.domain.SearchQuery;

import javax.ejb.Local;
import java.util.List;

@Local
public interface QueryService {

    /**
     * User History of executed queries
     *
     * @throws org.notes.common.exceptions.NotesException
     */
    List<SearchQuery> history() throws NotesException;

    void log(String query) throws NotesException;

}
