package org.notes.common.model;

import java.io.Serializable;

public interface FullText extends Serializable {

    Integer getSection();

    String getText();
}
