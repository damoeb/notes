package org.notes.common.model;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Collection;

public interface FullTextProvider {
    long getId();

    Long getOwnerId();

    Long getFolderId();

    // avoid invokes from json-mapper cause no session context available
    @JsonIgnore
    Collection<FullText> getFullTexts();
}
