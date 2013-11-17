package org.notes.search.interfaces;

import org.notes.common.model.Kind;

import java.util.Date;

public interface Indexable {

    long getId();

    String getTitle();

    String getFulltext();

    Kind getKind();

    Date getModified();

    Long getOwnerId();

    // todo implement? Collection<Long> getFolderIds();
    Long getFolderId();
    // todo implement! Long getDatabaseId();
}
