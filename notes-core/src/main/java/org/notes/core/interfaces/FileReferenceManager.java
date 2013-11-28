package org.notes.core.interfaces;

import org.apache.commons.fileupload.FileItem;
import org.notes.common.exceptions.NotesException;
import org.notes.common.interfaces.HarvestManager;
import org.notes.common.model.FileReference;

import javax.ejb.Local;
import java.io.File;

@Local
public interface FileReferenceManager extends HarvestManager {

    FileReference getFileReference(Long fileId) throws NotesException;

    File getRepository();

    FileReference store(FileItem item) throws NotesException;

}
