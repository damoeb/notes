package org.notes.common.domain;

import java.util.Date;

public interface Folder {

    long getId();

    Folder getParent();

    String getOwner();

    Integer getLevel();

    void setLeaf(Boolean isLeaf);

    void setLevel(Integer level);

    void setParent(Folder parent);

    void setModified(Date date);

    String getName();

    Boolean isExpanded();

    // todo distinguish count between documents in subfolder or direct

    Integer getDocumentCount();

    void setDocumentCount(Integer documentCount);
}
