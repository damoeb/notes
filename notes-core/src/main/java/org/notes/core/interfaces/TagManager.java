package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.core.model.Tag;

import javax.ejb.Local;

@Local
public interface TagManager {

    Tag findOrCreate(String name) throws NotesException;

}
