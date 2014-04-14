package org.notes.core.domain;

import org.notes.common.domain.Document;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by damoeb on 4/14/14.
 */
public class PostProcessEvent implements Serializable {
    private Collection<Document> documents;
    private EventType type;

    public PostProcessEvent(Collection<Document> documents, EventType type) {
        this.documents = documents;
        this.type = type;
    }

    public Collection<Document> getDocuments() {
        return documents;
    }

    public EventType getType() {
        return type;
    }
}
