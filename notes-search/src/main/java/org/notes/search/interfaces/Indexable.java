package org.notes.search.interfaces;

import org.notes.common.model.Kind;

import java.util.Date;

public interface Indexable {

    long getId();

    String getTitle();

    Kind getKind();

    Date getModified();

    Long getOwnerId();

    Long getFolderId();

    // todo implement! Long getDatabaseId();
}
