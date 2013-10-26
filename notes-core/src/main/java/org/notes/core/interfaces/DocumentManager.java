package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.core.dao.RepositoryFile;
import org.notes.core.model.Attachment;
import org.notes.core.model.Document;
import org.notes.core.model.TextDocument;

import javax.ejb.Local;
import java.util.List;

@Local
public interface DocumentManager {

    List<Document> getList(int firstResult, int maxResults) throws NotesException;

    Document getDocument(long documentId) throws NotesException;

    Document updateDocument(long documentId, Document document) throws NotesException;

    Document getByIdWithRefs(long documentId) throws NotesException;

    void removeAttachmentFromNote(long attachmentId, long documentId) throws NotesException;

    Attachment addAttachmentToNote(String fileName, RepositoryFile repositoryFile, Document document) throws NotesException;

    Attachment renameAttachment(long attachmentId, String newName) throws NotesException;

    Attachment getAttachmentWithFile(long attachmentId) throws NotesException;


    TextDocument createTextDocument(TextDocument document) throws NotesException;

    void deleteDocument(long documentId) throws NotesException;
}
