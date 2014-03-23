package org.notes.core.interfaces;

import org.apache.commons.fileupload.FileItem;
import org.notes.common.exceptions.NotesException;
import org.notes.core.model.StandardFileReference;

import javax.ejb.Local;
import java.io.File;

@Local
public interface FileReferenceManager {

    StandardFileReference getFileReference(Long fileId) throws NotesException;

    File getRepository();

    StandardFileReference store(FileItem item) throws NotesException;

}
