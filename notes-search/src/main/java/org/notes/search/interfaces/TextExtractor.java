package org.notes.search.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.common.model.FileReference;

import java.util.List;

public interface TextExtractor {

    String[] getContentTypes();

    List<String> extract(FileReference file) throws NotesException;
}
