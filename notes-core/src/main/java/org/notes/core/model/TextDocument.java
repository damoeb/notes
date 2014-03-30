package org.notes.core.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.Type;
import org.notes.common.exceptions.NotesException;
import org.notes.common.model.FullText;
import org.notes.common.model.Kind;
import org.notes.common.utils.TextUtils;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Entity(name = "TextDocument")
@Table(name = "TextDocument")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TextDocument extends BasicDocument {

    @Type(type = "org.hibernate.type.TextType")
    private String text;

//  --------------------------------------------------------------------------------------------------------------------

    public TextDocument() {
        // default
        setKind(Kind.TEXT);
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
    @JsonIgnore
    public Collection<FullText> getTexts() {
        List<FullText> list = new LinkedList<>();
        list.add(new StandardFullText(0, text));
        return list;
    }

    @Override
    public void validate() throws NotesException {
        super.validate();

        text = TextUtils.cleanHtmlRelaxed(text);
    }
}
