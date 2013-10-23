package org.notes.core.interfaces;

import org.notes.core.model.Folder;

import javax.ejb.Local;
import java.util.List;

@Local
public interface FolderManager {

    Folder createDatabase(String name);
    List<Folder> getDatabases();

    Folder getById(Long folderId);
    Folder createFolder(Long parentId, String name);
    Folder renameFolder(Long folderId, String name);
    Folder removeFolder(Long folderId);
    List<Folder> getChildren(Long folderId);
    Folder moveFolder(Long folderId, Long newParentId);
    Folder moveNote(Long noteId, Long newParentId);
}
