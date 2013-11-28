package org.notes.common.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.common.model.FileReference;

public interface HarvestManager {
    FileReference storeTemporary(String pathToSnapshot) throws NotesException;
}
