package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.core.model.BasicDocument;
import org.notes.core.model.Folder;

import javax.ejb.Local;
import java.util.List;

@Local
public interface FolderManager {

    Folder createFolder(Folder folder) throws NotesException;

    Folder getFolder(long folderId) throws NotesException;

    Folder deleteFolder(Folder folder) throws NotesException;

    Folder updateFolder(Folder folder) throws NotesException;

    List<BasicDocument> getDocuments(Long folderId) throws NotesException;

    List<BasicDocument> getRelatedDocuments(Long folderId, int offset, int count) throws NotesException;
}
