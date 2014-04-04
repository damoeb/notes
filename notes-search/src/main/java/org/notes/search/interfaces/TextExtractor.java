package org.notes.search.interfaces;

import org.notes.common.domain.FileReference;
import org.notes.common.exceptions.NotesException;
import org.notes.search.ExtractionResult;

public interface TextExtractor {

    ExtractionResult extract(FileReference file) throws NotesException;
}
