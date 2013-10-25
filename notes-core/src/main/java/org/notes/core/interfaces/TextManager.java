package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.core.model.FileReference;

import javax.ejb.Asynchronous;
import javax.ejb.Local;
import java.util.concurrent.Future;

@Local
public interface TextManager {

    @Asynchronous
    Future<String> extractAsync(FileReference reference) throws NotesException;
}
