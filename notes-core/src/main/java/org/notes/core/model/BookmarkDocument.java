package org.notes.core.model;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.common.ForeignKey;
import org.notes.common.interfaces.Harvestable;
import org.notes.common.model.FileReference;
import org.notes.common.model.FullText;
import org.notes.common.model.Kind;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collection;

@Entity(name = "BookmarkDocument")
@Table(name = "BookmarkDocument")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class BookmarkDocument extends BasicDocument implements Harvestable {

    private static final Logger LOGGER = Logger.getLogger(BookmarkDocument.class);

//  -- References ------------------------------------------------------------------------------------------------------

    @JsonIgnore
    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, optional = true, targetEntity = DefaultFileReference.class)
    @JoinColumn(name = ForeignKey.FILE_REFERENCE_ID)
    private FileReference siteSnapshot;

    @Column(insertable = false, updatable = false, name = ForeignKey.FILE_REFERENCE_ID, nullable = true)
    private Long siteSnapshotId;

//  --------------------------------------------------------------------------------------------------------------------

    @Basic
    @Column(nullable = false, length = 1024)
    private String url;

    @Lob
    private String text;

    public BookmarkDocument() {
        // default
        setKind(Kind.BOOKMARK);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String fullText) {
        this.text = fullText;
    }

    public FileReference getSiteSnapshot() {
        return siteSnapshot;
    }

    public void setSiteSnapshot(FileReference siteSnapshot) {
        this.siteSnapshot = siteSnapshot;
    }

    public Long getSiteSnapshotId() {
        return siteSnapshotId;
    }

    public void setSiteSnapshotId(Long siteSnapshotId) {
        this.siteSnapshotId = siteSnapshotId;
    }

    @JsonIgnore
    @Override
    public Collection<FullText> getTexts() {
        return Arrays.asList(new FullText(1, text));
    }
}
