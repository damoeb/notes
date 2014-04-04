package org.notes.core.services;

import org.notes.common.domain.Tag;
import org.notes.common.exceptions.NotesException;
import org.notes.core.domain.BasicDocument;
import org.notes.core.domain.StandardTag;

import javax.ejb.Local;
import java.util.Collection;

@Local
public interface TagService {

    StandardTag findOrCreate(String name) throws NotesException;

    Collection<Tag> getRecommendations(BasicDocument document) throws NotesException;

    Collection<Tag> getTagNetwork() throws NotesException;
}
