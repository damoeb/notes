package org.notes.core.model;

import javax.persistence.Lob;

//@Entity(name = "TextDocument")
//@Table(name = "TextDocument")
////@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TextDocument /* extends Document */ {

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
