package org.notes.core.services;

import org.notes.common.domain.Document;
import org.notes.common.exceptions.NotesException;

import javax.ejb.Remote;
import java.util.Collection;

@Remote
public interface SearchServiceRemote {

    /**
     * Have documents indexed
     *
     * @param documents
     * @throws org.notes.common.exceptions.NotesException
     */
    void index(Collection<Document> documents) throws NotesException;

    /**
     * Remove documents from index
     *
     * @param documents
     * @throws NotesException
     */
    void deleteFromIndex(Collection<Document> documents) throws NotesException;
}
