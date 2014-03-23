package org.notes.core.model;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.Type;
import org.notes.common.ForeignKey;
import org.notes.common.model.FileReference;
import org.notes.common.model.FullText;
import org.notes.common.model.Kind;
import org.notes.common.utils.TextUtils;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Entity(name = "BookmarkDocument")
@Table(name = "BookmarkDocument")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class BookmarkDocument extends BasicDocument {

    private static final Logger LOGGER = Logger.getLogger(BookmarkDocument.class);

//  -- References ------------------------------------------------------------------------------------------------------

    @JsonIgnore
    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, optional = true, targetEntity = StandardFileReference.class)
    @JoinColumn(name = ForeignKey.FILE_REFERENCE_ID)
    private FileReference siteSnapshot;

    @Column(insertable = false, updatable = false, name = ForeignKey.FILE_REFERENCE_ID, nullable = true)
    private Long siteSnapshotId;

//  --------------------------------------------------------------------------------------------------------------------

    @Basic
    @Column(nullable = false, length = 1024)
    private String url;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
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

    @PrePersist
    @PreUpdate
    @Override
    public void onPersist() {
        super.onPersist();
        String url = "<div class=\"url\">" + removeProtocol(getUrl()) + "</div>";
        String text = StringUtils.substring(TextUtils.toOutline(getText()), 0, 256);
        setOutline(url + text);
    }

    private String removeProtocol(String url) {
        return url.replaceFirst("^[a-zA-Z]+://", "");
    }

    @JsonIgnore
    @Override
    public Collection<FullText> getTexts() {
        List<FullText> list = new LinkedList<>();
        list.add(new StandardFullText(0, text));
        return list;
    }
}
