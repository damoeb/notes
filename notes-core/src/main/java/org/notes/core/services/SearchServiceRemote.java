package org.notes.core.services;

import org.notes.common.domain.Document;
import org.notes.common.exceptions.NotesException;

import javax.ejb.Remote;

@Remote
public interface SearchServiceRemote {

    /**
     * Have document indexed
     *
     * @param document
     * @throws org.notes.common.exceptions.NotesException
     */
    void index(Document document) throws NotesException;

}
