package org.notes.common.interfaces;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.notes.common.model.FullText;

import java.util.Collection;

public interface Fulltextable extends Document {

    // avoid calls from json mapper - no session context available
    @JsonIgnore
    Collection<FullText> getFullTexts();
}
