package org.notes.common.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.common.model.Database;
import org.notes.common.model.Folder;

import javax.ejb.Local;
import java.util.List;

@Local
public interface FolderManager {

    Folder createFolder(Folder folder, Folder parent, Database database) throws NotesException;

    Folder getFolder(long folderId) throws NotesException;

    Folder deleteFolder(long folderId) throws NotesException;

    Folder updateFolder(long folderId, Folder folder) throws NotesException;

    List<Folder> getChildren(long folderId) throws NotesException;

    List<Folder> getParents(Document document) throws NotesException;
}
