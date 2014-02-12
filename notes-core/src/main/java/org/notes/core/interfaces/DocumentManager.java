package org.notes.core.interfaces;

import org.apache.commons.fileupload.FileItem;
import org.notes.common.exceptions.NotesException;
import org.notes.core.model.*;

import javax.ejb.Local;
import java.util.List;

@Local
public interface DocumentManager {

    TextDocument createDocument(TextDocument document, Folder inFolder) throws NotesException;

    List<BasicDocument> getDocumentsInFolder(Long folderId) throws NotesException;

    BasicDocument getDocument(long documentId) throws NotesException;

    BasicDocument deleteDocument(long documentId) throws NotesException;

    BasicDocument updateDocument(BasicDocument document) throws NotesException;

    PdfDocument uploadDocument(List<FileItem> items) throws NotesException;

    BookmarkDocument bookmark(BookmarkDocument bookmark, Folder folder) throws NotesException;
}
