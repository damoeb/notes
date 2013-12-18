package org.notes.core.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.common.ForeignKey;
import org.notes.common.interfaces.Fulltextable;
import org.notes.common.model.FullText;
import org.notes.common.model.Kind;
import org.notes.common.utils.TextUtils;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "TextDocument")
@Table(name = "TextDocument")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TextDocument extends BasicDocument implements Fulltextable {

    @Lob
    private String text;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = ForeignKey.DOCUMENT_ID)
    private Set<Change> history = new HashSet(50);

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
    public Collection<FullText> getFullTexts() {
        return Arrays.asList(new FullText(0, text));
    }

    public Set<Change> getHistory() {
        return history;
    }

    public void setHistory(Set<Change> history) {
        this.history = history;
    }
}
