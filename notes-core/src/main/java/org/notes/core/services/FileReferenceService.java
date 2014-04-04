package org.notes.core.services;

import org.apache.commons.fileupload.FileItem;
import org.notes.common.exceptions.NotesException;
import org.notes.core.domain.StandardFileReference;

import javax.ejb.Local;
import java.io.File;

@Local
public interface FileReferenceService {

    StandardFileReference getFileReference(Long fileId) throws NotesException;

    File getRepository();

    StandardFileReference store(FileItem item) throws NotesException;

}
