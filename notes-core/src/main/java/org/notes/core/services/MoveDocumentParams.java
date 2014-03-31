package org.notes.core.services;

import java.util.List;

public class MoveDocumentParams {

    private List<Long> documentIds;
    private Long toFolderId;

    public List<Long> getDocumentIds() {
        return documentIds;
    }

    public void setDocumentIds(List<Long> documentIds) {
        this.documentIds = documentIds;
    }

    public Long getToFolderId() {
        return toFolderId;
    }

    public void setToFolderId(Long toFolderId) {
        this.toFolderId = toFolderId;
    }
}
