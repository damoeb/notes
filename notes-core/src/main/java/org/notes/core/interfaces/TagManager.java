package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.common.model.Tag;
import org.notes.core.model.BasicDocument;
import org.notes.core.model.StandardTag;

import javax.ejb.Local;
import java.util.Collection;

@Local
public interface TagManager {

    StandardTag findOrCreate(String name) throws NotesException;

    Collection<Tag> getRecommendations(BasicDocument document) throws NotesException;

    Collection<Tag> getTagNetwork() throws NotesException;
}
