package org.notes.common.services;

import org.notes.common.domain.Database;
import org.notes.common.domain.Document;
import org.notes.common.domain.Folder;
import org.notes.common.exceptions.NotesException;

import javax.ejb.Local;
import java.util.List;

@Local
public interface FolderService {

    Folder createFolder(Folder folder, Folder parent, Database database) throws NotesException;

    Folder getFolder(long folderId) throws NotesException;

    void deleteFolder(long folderId) throws NotesException;

    Folder updateFolder(long folderId, Folder folder) throws NotesException;

    List<Folder> getChildren(long folderId) throws NotesException;

    List<Folder> getParents(Document document) throws NotesException;
}
