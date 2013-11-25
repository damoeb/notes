package org.notes.text.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.common.model.FileReference;
import org.notes.text.ExtractionResult;

public interface TextExtractor {

    ExtractionResult extract(FileReference file) throws NotesException;
}
