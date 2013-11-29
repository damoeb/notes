package org.notes.common.interfaces;

import org.notes.common.model.Kind;
import org.notes.common.model.Trigger;

import java.io.Serializable;
import java.util.Date;

public interface Document extends Serializable {

    final String QUERY_BY_ID = "BasicDocument.QUERY_BY_ID";
    final String QUERY_TRIGGER = "BasicDocument.QUERY_TRIGGER";

    long getId();

    String getTitle();

    Date getCreated();

    Date getModified();

    Long getFolderId();

    Long getOwnerId();

    Kind getKind();

    boolean isDeleted();

    Trigger getTrigger();

    void setTrigger(Trigger trigger);

    Date getFinished();
}