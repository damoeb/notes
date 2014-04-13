package org.notes.common.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

public interface Document extends Serializable {

    final String QUERY_BY_ID = "BasicDocument.QUERY_BY_ID";

    long getId();

    String getTitle();

    Date getCreated();

    Date getModified();

    Long getFolderId();

    String getUserId();

    String getUrl();

    Kind getKind();

    String getOutline();

    boolean isStar();

    boolean isDeleted();

    void setDeleted(boolean deleted);

    Set<Tag> getTags();

    String getUniqueHash();

    Collection<FullText> getTexts();
}
