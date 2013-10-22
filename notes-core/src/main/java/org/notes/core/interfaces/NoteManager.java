package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.core.dao.RepositoryFile;
import org.notes.core.model.Attachment;
import org.notes.core.model.FileReference;
import org.notes.core.model.Note;

import javax.ejb.Local;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.io.InputStream;
import java.util.List;

@Local
public interface NoteManager {

    Note addNote(Note note) throws NotesException;

    List<Note> getList(int firstResult, int maxResults) throws NotesException;

    Note getById(long noteId) throws NotesException;

    Note updateNote(long noteId, Note note) throws NotesException;

    Note getByIdWithRefs(long noteId) throws NotesException;

    void removeNote(long noteId) throws NotesException;

    void removeAttachmentFromNote(long attachmentId, long noteId) throws NotesException;

    Attachment addAttachmentToNote(String fileName, RepositoryFile repositoryFile, Note note) throws NotesException;

    Attachment renameAttachment(long attachmentId, String newName) throws NotesException;

    Attachment getAttachmentWithFile(long attachmentId) throws NotesException;
}
