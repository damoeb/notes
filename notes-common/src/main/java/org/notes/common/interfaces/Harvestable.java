package org.notes.common.interfaces;

import org.notes.common.model.FileReference;

public interface Harvestable extends Document {

    String getUrl();

    FileReference getSiteSnapshot();

    void setSiteSnapshot(FileReference siteSnapshot);

    Long getSiteSnapshotId();
}
