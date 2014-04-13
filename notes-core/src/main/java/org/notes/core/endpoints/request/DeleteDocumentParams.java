package org.notes.core.endpoints.request;

import java.util.List;

public class DeleteDocumentParams {

    private List<Long> documentIds;

    public List<Long> getDocumentIds() {
        return documentIds;
    }

    public void setDocumentIds(List<Long> documentIds) {
        this.documentIds = documentIds;
    }
}
