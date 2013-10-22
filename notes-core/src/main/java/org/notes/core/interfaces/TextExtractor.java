package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.core.model.FileReference;

import javax.ejb.Asynchronous;
import javax.ejb.Local;
import java.util.List;
import java.util.concurrent.Future;

public interface TextExtractor {

    String[] getContentTypes();
    List<String> extract(FileReference file) throws NotesException;
}
