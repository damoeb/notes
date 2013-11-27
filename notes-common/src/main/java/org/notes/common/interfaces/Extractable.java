package org.notes.common.interfaces;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.notes.common.exceptions.NotesException;
import org.notes.common.model.FullText;

import java.util.Collection;

public interface Extractable {

    /**
     * extract any fulltext required from any descendents
     *
     * @throws org.notes.common.exceptions.NotesException
     *
     */
    void extract() throws NotesException;

    // avoid calls from json mapper - no session context available
    @JsonIgnore
    Collection<FullText> getFullTexts();

}
