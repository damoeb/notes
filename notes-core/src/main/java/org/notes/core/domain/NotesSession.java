package org.notes.core.domain;

import java.io.Serializable;
import java.util.Set;

//@Remote
public interface NotesSession extends Serializable {

    String getUserId();

    Long getDefaultFolderId();

    Long getTrashFolderId();

    Long getActiveFolderId();

    Set<StandardDatabase> getDatabases();

    void setDatabases(Set<StandardDatabase> databases);

    void setUserId(String userId);

    void setActiveFolderId(Long activeFolderId);

    void setDefaultFolderId(Long defaultFolderId);

    void setTrashFolderId(Long trashFolderId);
}
