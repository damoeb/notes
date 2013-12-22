package org.notes.search.model;

import org.apache.solr.common.SolrDocument;
import org.notes.common.model.IndexFields;
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

    public DocumentHit(SolrDocument solrDocument) {
        this.documentId = (Long) solrDocument.getFirstValue(IndexFields.DOCUMENT);
        this.modified = (Date) solrDocument.getFirstValue(IndexFields.MODIFIED);
        this.title = (String) solrDocument.getFirstValue(IndexFields.TITLE);
        this.kind = Kind.valueOf((String) solrDocument.getFirstValue(IndexFields.KIND));
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
