package org.notes.search.model;

import org.apache.solr.common.SolrDocument;
import org.notes.common.model.Kind;
import org.notes.common.model.SolrFields;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DocumentHit {

    private Float score;
    private Long documentId;
    private Date modified;
    private String title;
    private String owner;
    private List<String> highlights;
    private Kind kind;

    public DocumentHit(SolrDocument solrDocument, Map<String, List<String>> highlights) {

        this.documentId = (Long) solrDocument.getFirstValue(SolrFields.DOCUMENT);
        this.modified = (Date) solrDocument.getFirstValue(SolrFields.MODIFIED);
        this.title = (String) solrDocument.getFirstValue(SolrFields.TITLE);
        this.owner = (String) solrDocument.getFirstValue(SolrFields.OWNER);
        this.kind = Kind.valueOf((String) solrDocument.getFirstValue(SolrFields.KIND));
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

    public String getOwner() {
        return owner;
    }

    public List<String> getHighlights() {
        return highlights;
    }

    public Kind getKind() {
        return kind;
    }
}
