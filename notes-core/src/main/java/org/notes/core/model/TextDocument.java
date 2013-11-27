package org.notes.core.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.common.exceptions.NotesException;
import org.notes.common.interfaces.Extractable;
import org.notes.common.model.Document;
import org.notes.common.model.FullText;
import org.notes.common.utils.TextUtils;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.Collection;

@Entity(name = "TextDocument")
@Table(name = "TextDocument")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TextDocument extends Document implements Extractable {

    @Lob
    private String text;

//  --------------------------------------------------------------------------------------------------------------------

    public TextDocument() {
        // default
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void onPersist() {
        super.onPersist();
        setOutline(TextUtils.toOutline(getText()));
    }

    @Override
    public void extract() throws NotesException {
        // not used
    }

    @Override
    @JsonIgnore
    public Collection<FullText> getFullTexts() {
        return Arrays.asList(new FullText(0, text));
    }
}
