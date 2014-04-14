package org.notes.core.services;

import org.apache.commons.fileupload.FileItem;
import org.notes.common.domain.Folder;
import org.notes.common.exceptions.NotesException;
import org.notes.core.domain.BasicDocument;
import org.notes.core.domain.PdfDocument;
import org.notes.core.domain.TextDocument;

import javax.ejb.Local;
import java.util.List;

@Local
public interface DocumentService {

    TextDocument createDocument(TextDocument document, Folder inFolder) throws NotesException;

    List<BasicDocument> getDocumentsInFolder(Long folderId, int start, int rows) throws NotesException;

    BasicDocument getDocument(long documentId) throws NotesException;

    void deleteDocument(long documentId) throws NotesException;

    BasicDocument updateBasicDocument(BasicDocument document) throws NotesException;

    BasicDocument updateTextDocument(TextDocument txtRef) throws NotesException;

    PdfDocument uploadDocument(List<FileItem> items) throws NotesException;

    void moveTo(List<Long> documentId, Long folderId) throws NotesException;

    void delete(List<Long> documentIds) throws NotesException;
}
