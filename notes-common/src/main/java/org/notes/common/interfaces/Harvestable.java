package org.notes.common.interfaces;

import org.notes.common.model.FileReference;

public interface Harvestable extends Document {

    String getUrl();

    void setText(String text);

    FileReference getSiteSnapshot();

    void setSiteSnapshot(FileReference siteSnapshot);

    Long getSiteSnapshotId();

    void setTitle(String title);

    void setOutline(String url);
}
