package org.notes.common.domain;

import java.io.Serializable;
import java.util.Set;

public interface Database {
    long getId();

    Serializable getUserId();

    Set<Folder> getFolders();

}
