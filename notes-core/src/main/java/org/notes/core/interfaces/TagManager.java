package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.common.model.Tag;
import org.notes.core.model.DefaultTag;
import org.notes.core.model.TextDocument;

import javax.ejb.Local;
import java.util.Collection;

@Local
public interface TagManager {

    DefaultTag findOrCreate(String name) throws NotesException;

    Collection<Tag> getRecommendations(TextDocument document) throws NotesException;

    Collection<Tag> getTagNetwork() throws NotesException;
}
