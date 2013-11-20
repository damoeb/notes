package org.notes.common.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "File")
@Table(name = "File",
        uniqueConstraints = @UniqueConstraint(columnNames = {"checksum", "size"})
)
@NamedQueries({
        @NamedQuery(name = FileReference.QUERY_BY_ID, query = "SELECT a FROM File a where a.id=:ID"),
        @NamedQuery(name = FileReference.QUERY_BY_CHECKSUM, query = "SELECT a FROM File a where a.checksum=:CHECKSUM and a.size=:FILESIZE")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class FileReference implements Serializable {

    public static final String QUERY_BY_ID = "FileReference.QUERY_BY_ID";
    public static final String FK_FILE_REFERENCE_ID = "fileref_id";
    public static final String QUERY_BY_CHECKSUM = "FileReference.QUERY_BY_CHECKSUM";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Basic
    @Index(name = "checksumIdx")
    @Column(name = "checksum", nullable = false)
    private String checksum;

    @JsonIgnore
    @Basic
    @Column(nullable = false)
    private String reference;

    @Basic
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Lob
    @Column(name = "full_text")
    private String fullText;

    @Basic
    @Column(name = "size", nullable = false)
    private long size;

    public FileReference() {
        //
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
}
