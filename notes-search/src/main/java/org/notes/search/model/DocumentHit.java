package org.notes.search.model;

import org.apache.solr.common.SolrDocument;
import org.notes.common.model.IndexFields;
import org.notes.common.model.Kind;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DocumentHit {

    private Float score;
    private Long documentId;
    private Date modified;
    private String title;
    private List<String> highlights;
    private Kind kind;

    public DocumentHit(SolrDocument solrDocument, Map<String, List<String>> highlights) {

        this.documentId = (Long) solrDocument.getFirstValue(IndexFields.DOCUMENT);
        this.modified = (Date) solrDocument.getFirstValue(IndexFields.MODIFIED);
        this.title = (String) solrDocument.getFirstValue(IndexFields.TITLE);
        this.kind = Kind.valueOf((String) solrDocument.getFirstValue(IndexFields.KIND));
        this.score = (Float) solrDocument.getFirstValue("score");
        this.highlights = new LinkedList<>();
        if (highlights != null) {
            for (List<String> highlight : highlights.values()) {
                this.highlights.addAll(highlight);
            }
        }
    }

    public Float getScore() {
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

    public List<String> getHighlights() {
        return highlights;
    }

    public Kind getKind() {
        return kind;
    }
}
