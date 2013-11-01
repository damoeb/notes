package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.core.model.Document;

import javax.ejb.Local;

@Local
public interface DocumentManager<T extends Document> {

    T createDocument(T document) throws NotesException;

    T getDocument(long documentId) throws NotesException;

    T deleteDocument(T document) throws NotesException;

    T updateDocument(T document) throws NotesException;

}
