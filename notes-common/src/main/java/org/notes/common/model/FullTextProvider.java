package org.notes.common.model;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Collection;

public interface FullTextProvider {
    long getId();

    Long getOwnerId();

    Long getFolderId();

    @JsonIgnore
        // required, jsonmapper will call this method without session context
    Collection<FullText> getFullTexts();
}
