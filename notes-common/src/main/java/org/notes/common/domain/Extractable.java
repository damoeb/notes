package org.notes.common.domain;

import org.notes.common.exceptions.NotesException;

public interface Extractable extends Document {

    /**
     * extract any fulltext required from any descendents
     *
     * @throws org.notes.common.exceptions.NotesException
     */
    void extract() throws NotesException;
}
