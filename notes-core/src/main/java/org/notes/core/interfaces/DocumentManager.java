package org.notes.core.interfaces;

import org.apache.commons.fileupload.FileItem;
import org.notes.common.exceptions.NotesException;
import org.notes.common.model.Folder;
import org.notes.core.model.BasicDocument;
import org.notes.core.model.PdfDocument;
import org.notes.core.model.TextDocument;

import javax.ejb.Local;
import java.util.List;

@Local
public interface DocumentManager {

    TextDocument createDocument(TextDocument document, Folder inFolder) throws NotesException;

    List<BasicDocument> getDocumentsInFolder(Long folderId) throws NotesException;

    BasicDocument getDocument(long documentId) throws NotesException;

    BasicDocument deleteDocument(long documentId) throws NotesException;

    BasicDocument updateBasicDocument(BasicDocument document) throws NotesException;

    BasicDocument updateTextDocument(TextDocument txtRef) throws NotesException;

    PdfDocument uploadDocument(List<FileItem> items) throws NotesException;

    void moveTo(Long documentId, Long folderId) throws NotesException;
}
