package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.core.model.Folder;

import javax.ejb.Local;
import java.util.List;

@Local
public interface FolderManager {

    Folder createDatabase(Folder folder) throws NotesException;

    Folder getDatabase(long folderId) throws NotesException;

    Folder deleteDatabase(long folderId) throws NotesException;

    Folder updateDatabase(long folderId, Folder folder) throws NotesException;

    List<Folder> getDatabases() throws NotesException;


    Folder createFolder(Folder folder) throws NotesException;

    Folder getFolder(long folderId) throws NotesException;

    Folder deleteFolder(long folderId) throws NotesException;

    Folder updateFolder(long folderId, Folder folder) throws NotesException;
    /*
    List<Folder> getDatabases();

    Folder getById(Long folderId);
    Folder createFolder(Long parentId, String name);
    Folder renameFolder(Long folderId, String name);
    Folder removeFolder(Long folderId);
    List<Folder> getChildren(Long folderId);
    Folder moveFolder(Long folderId, Long newParentId);
    Folder moveNote(Long noteId, Long newParentId);
    */
}
