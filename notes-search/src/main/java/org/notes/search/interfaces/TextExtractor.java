package org.notes.search.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.common.model.FileReference;

public interface TextExtractor {

    String extract(FileReference file) throws NotesException;
}
