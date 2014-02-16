package org.notes.common.model;

import java.io.Serializable;
import java.util.Set;

public interface FileReference extends Serializable {
    String getReference();

    long getSize();

    Set<FullText> getFullTexts();
}
