package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.core.model.DefaultTag;

import javax.ejb.Local;

@Local
public interface TagManager {

    DefaultTag findOrCreate(String name) throws NotesException;

}
