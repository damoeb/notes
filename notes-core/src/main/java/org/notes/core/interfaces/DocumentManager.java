package org.notes.core.interfaces;

import org.apache.commons.fileupload.FileItem;
import org.notes.common.exceptions.NotesException;
import org.notes.core.model.Document;
import org.notes.core.model.PdfDocument;
import org.notes.core.model.TextDocument;

import javax.ejb.Local;
import java.util.List;

@Local
public interface DocumentManager {

    TextDocument createDocument(TextDocument document) throws NotesException;

    Document getDocument(long documentId) throws NotesException;

    Document deleteDocument(Document document) throws NotesException;

    Document updateDocument(Document document) throws NotesException;

    PdfDocument uploadDocument(List<FileItem> items) throws NotesException;
}
