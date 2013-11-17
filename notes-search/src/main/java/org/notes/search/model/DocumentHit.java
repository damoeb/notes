package org.notes.search.model;

import org.notes.common.model.Kind;

import java.util.Date;

public class DocumentHit {

    private Double score;
    private Long documentId;
    private Date modified;
    private String title;
    private String highlights;
    private Kind kind;

    public DocumentHit(Double score, Long documentId, Date modified, String title, String highlights, Kind kind) {
        this.score = score;
        this.documentId = documentId;
        this.modified = modified;
        this.title = title;
        this.highlights = highlights;
        this.kind = kind;
    }

    public Double getScore() {
        return score;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public Date getModified() {
        return modified;
    }

    public String getTitle() {
        return title;
    }

    public String getHighlights() {
        return highlights;
    }

    public Kind getKind() {
        return kind;
    }
}
