package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.core.model.FileDocument;

import javax.ejb.Local;

@Local
public interface FileDocumentManager extends DocumentManager<FileDocument> {

    FileDocument createDocument(FileDocument document) throws NotesException;

    FileDocument getDocument(long documentId) throws NotesException;

    FileDocument deleteDocument(FileDocument document) throws NotesException;

    FileDocument updateDocument(FileDocument document) throws NotesException;
}
