package org.notes.search.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.common.model.ContentType;
import org.notes.common.model.FileReference;
import org.notes.search.model.ExtractionResult;

import java.util.Collection;

public interface TextExtractor {

    Collection<ContentType> getSupported();

    ExtractionResult extract(FileReference file) throws NotesException;
}
