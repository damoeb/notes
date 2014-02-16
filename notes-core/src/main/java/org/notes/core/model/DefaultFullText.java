package org.notes.core.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.notes.common.ForeignKey;
import org.notes.common.model.FullText;

import javax.persistence.*;

@Entity(name = "FullTextOfFile")
@Table(name = "FullTextOfFile")
public class DefaultFullText implements FullText {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, name = "sectionId")
    private Integer section;

    @Column(name = ForeignKey.FILE_REFERENCE_ID, updatable = false, insertable = false)
    private Long fileReferenceId;

    @JsonIgnore
    @Lob
    private String text;

//  --------------------------------------------------------------------------------------------------------------------

    public DefaultFullText() {
        //
    }

    public DefaultFullText(Integer section, String text) {
        this.section = section;
        this.text = text;
    }

    @Override
    public Integer getSection() {
        return section;
    }

    public void setSection(Integer section) {
        this.section = section;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getFileReferenceId() {
        return fileReferenceId;
    }

    public void setFileReferenceId(Long fileReferenceId) {
        this.fileReferenceId = fileReferenceId;
    }
}
