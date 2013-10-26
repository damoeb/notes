package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.core.model.FileReference;

import java.util.List;

public interface TextExtractor {

    String[] getContentTypes();

    List<String> extract(FileReference file) throws NotesException;
}
