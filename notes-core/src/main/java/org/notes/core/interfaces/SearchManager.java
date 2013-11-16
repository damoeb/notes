package org.notes.core.interfaces;

import org.notes.common.exceptions.NotesException;
import org.notes.core.model.DocumentHit;

import javax.ejb.Local;
import java.util.List;

@Local
public interface SearchManager {

    List<DocumentHit> query(String query, Long databaseId, Long folderId) throws NotesException;
}
