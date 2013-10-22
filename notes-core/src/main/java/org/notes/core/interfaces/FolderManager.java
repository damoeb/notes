package org.notes.core.interfaces;

import org.notes.core.model.Folder;
import org.notes.core.model.Note;

import javax.ejb.Local;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Local
public interface FolderManager {

    Folder createNotebook(Long userId, String name);
    Folder getById(Long folderId);
    Folder createFolder(Long parentId, Long userId, String name);
    Folder renameFolder(Long folderId, String name);
    Folder removeFolder(Long folderId);
    List<Folder> getChildren(Long folderId);

}
