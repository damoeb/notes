package org.notes.search.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.common.model.FileReference;
import org.notes.search.ExtractionResult;

public interface TextExtractor {

    ExtractionResult extract(FileReference file) throws NotesException;
}
