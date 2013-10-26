package org.notes.core.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity(name = "TextDocument")
@Table(name = "TextDocument")
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TextDocument extends Document {

    @Lob
    private String text;

    public TextDocument() {
        // default
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
