package org.notes.core.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.Index;
import org.notes.common.model.FileReference;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "Attachment")
@Table(name = "Attachment")
@NamedQueries({
        @NamedQuery(name = Attachment.QUERY_BY_ID, query = "SELECT a FROM Attachment a where a.id=:ID")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Attachment implements Serializable {

    public static final String QUERY_BY_ID = "Attachment.QUERY_BY_ID";

    public static final String FK_ATTACHMENT_ID = "attachment_id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Basic
    @Index(name = "nameIdx")
    @Column(nullable = false)
    private String name;

    @Basic
    @Column
    private String description;

    @Basic
    @Column(nullable = false)
    private long size;

    @Basic
    @Column(nullable = false)
    private String contentType;

    @JsonIgnore
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = FileReference.FK_FILE_REFERENCE_ID)
    private FileReference fileReference;

    @Column(name = FileReference.FK_FILE_REFERENCE_ID, insertable = false, updatable = false)
    private Long fileReferenceId;

    public Attachment() {
        //
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FileReference getFileReference() {
        return fileReference;
    }

    public void setFileReference(FileReference fileReference) {
        this.fileReference = fileReference;
    }

    public Long getFileReferenceId() {
        return fileReferenceId;
    }

    public void setFileReferenceId(Long fileReferenceId) {
        this.fileReferenceId = fileReferenceId;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attachment)) return false;

        Attachment that = (Attachment) o;

        if (fileReferenceId != null ? !fileReferenceId.equals(that.fileReferenceId) : that.fileReferenceId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return fileReferenceId != null ? fileReferenceId.hashCode() : 0;
    }
}
