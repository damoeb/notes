package org.notes.core.interfaces;

import org.apache.commons.fileupload.FileItem;
import org.notes.common.exceptions.NotesException;
import org.notes.core.dao.RepositoryFile;
import org.notes.core.model.FileReference;
import org.notes.core.model.Note;

import javax.ejb.Local;
import java.io.File;
import java.util.List;

@Local
public interface FileManager {

    File getRepository();

    File getTempRepository();

    RepositoryFile storeInRepository(FileItem item);

    FileReference find(String checksum, long size) throws NotesException;
}
