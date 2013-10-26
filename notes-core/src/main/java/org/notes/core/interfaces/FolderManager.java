package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.core.model.Folder;

import javax.ejb.Local;

@Local
public interface FolderManager {

    Folder createFolder(Folder folder) throws NotesException;

    Folder getFolder(long folderId) throws NotesException;

    Folder deleteFolder(long folderId) throws NotesException;

    Folder updateFolder(long folderId, Folder folder) throws NotesException;
}
