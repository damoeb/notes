package org.notes.common.domain;

import java.io.Serializable;

public interface FullText extends Serializable {

    Integer getSection();

    String getText();
}
