package org.notes.common.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public interface Document extends Serializable {

    final String QUERY_BY_ID = "BasicDocument.QUERY_BY_ID";
    final String QUERY_TRIGGER = "BasicDocument.QUERY_TRIGGER";

    long getId();

    String getThumbnailUrl();

    String getTitle();

    Date getCreated();

    Date getModified();

    Long getFolderId();

    String getOwner();

    String getUrl();

    Kind getKind();

    Trigger getTrigger();

    void setTrigger(Trigger trigger);

    String getOutline();

    boolean isStar();

    Set<Tag> getTags();

    Map<String, Double> getEssence();

    void setEssence(Map<String, Double> essence);

    String getUniqueHash();

    Collection<FullText> getTexts();
}