package org.notes.core.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "Annotation")
@Table(name = "Annotation",
        uniqueConstraints = @UniqueConstraint(columnNames = {Attachment.FK_ATTACHMENT_ID, "position"})
)
@NamedQueries({
        @NamedQuery(name = Annotation.QUERY_BY_ID, query = "SELECT a FROM Annotation a where a.id=:ID")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Annotation implements Serializable {

    public static final String QUERY_BY_ID = "Annotation.QUERY_BY_ID";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Basic
    @Column(nullable = false)
    private String text;

    @Basic
    @Column(nullable = false)
    private long position;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = Attachment.FK_ATTACHMENT_ID)
    private Attachment attachment;

    @Column(name = Attachment.FK_ATTACHMENT_ID, insertable = false, updatable = false)
    private Long attachmentId;

    public Annotation() {
        //
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }
}
