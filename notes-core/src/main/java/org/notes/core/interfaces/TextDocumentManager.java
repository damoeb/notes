package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.core.model.TextDocument;

import javax.ejb.Local;

@Local
public interface TextDocumentManager extends DocumentManager<TextDocument> {

    TextDocument createDocument(TextDocument document) throws NotesException;

    TextDocument getDocument(long documentId) throws NotesException;

    TextDocument deleteDocument(TextDocument document) throws NotesException;

    TextDocument updateDocument(TextDocument document) throws NotesException;
}
