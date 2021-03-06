package org.notes.core.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.Index;
import org.notes.common.ForeignKey;
import org.notes.common.domain.FileReference;
import org.notes.common.domain.FullText;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "File")
@Table(name = "File",
        uniqueConstraints = @UniqueConstraint(columnNames = {"checksum", "size", "contentType"})
)
@NamedQueries({
        @NamedQuery(name = StandardFileReference.QUERY_BY_ID, query = "SELECT a FROM File a where a.id=:ID"),
        @NamedQuery(name = StandardFileReference.QUERY_BY_CHECKSUM, query = "SELECT a FROM File a where a.checksum=:CHECKSUM and a.size=:FILESIZE")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class StandardFileReference implements FileReference {

    public static final String QUERY_BY_ID = "FileReference.QUERY_BY_ID";
    public static final String QUERY_BY_CHECKSUM = "FileReference.QUERY_BY_CHECKSUM";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Basic
    @Index(name = "checksumIdx")
    @Column(nullable = false)
    private String checksum;

    @JsonIgnore
    @Basic
    @Column(nullable = false)
    private String reference;

    @Basic
    @Column(nullable = false)
    private String contentType;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = StandardFullText.class)
    @JoinColumn(name = ForeignKey.FILE_REFERENCE_ID)
    private Set<FullText> fullTexts = new HashSet(50);

    @Basic
    @Column(nullable = false)
    private long size;

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

//  --------------------------------------------------------------------------------------------------------------------

    public StandardFileReference() {
        //
    }

    @PrePersist
    @PreUpdate
    public void onPersist() {
        if (modified == null) {
            modified = new Date();
        }
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public Set<FullText> getFullTexts() {
        return fullTexts;
    }

    public void setFullTexts(Set<FullText> fullTexts) {
        this.fullTexts = fullTexts;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
}
