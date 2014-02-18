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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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

    @JsonIgnore
    @Lob
    private String text;

    public BookmarkDocument() {
        // default
        setKind(Kind.BOOKMARK);
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String fullText) {
        this.text = fullText;
    }

    @Override
    public FileReference getSiteSnapshot() {
        return siteSnapshot;
    }

    @Override
    public void setSiteSnapshot(FileReference siteSnapshot) {
        this.siteSnapshot = siteSnapshot;
    }

    @Override
    public Long getSiteSnapshotId() {
        return siteSnapshotId;
    }

    @Override
    public String getDomain() {
        try {
            return new URL(getUrl()).getHost();
        } catch (MalformedURLException e) {
            // should not happen
            throw new IllegalArgumentException(e);
        }
    }

    public void setSiteSnapshotId(Long siteSnapshotId) {
        this.siteSnapshotId = siteSnapshotId;
    }

    @JsonIgnore
    @Override
    public Collection<FullText> getTexts() {
        List<FullText> list = new LinkedList<>();
        list.add(new DefaultFullText(0, text));
        return list;
    }
}
