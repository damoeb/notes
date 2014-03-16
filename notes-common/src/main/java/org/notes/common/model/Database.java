package org.notes.common.model;

import java.io.Serializable;
import java.util.Set;

public interface Database {
    long getId();

    Serializable getOwner();

    Set<Folder> getFolders();

}
