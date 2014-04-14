package org.notes.core.domain;

import org.apache.solr.common.SolrDocument;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.common.configuration.SolrFields;
import org.notes.common.domain.Kind;
import org.notes.common.endpoints.CustomDateDeserializer;
import org.notes.common.endpoints.CustomDateSerializer;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SearchHit {

    private final float score;

    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonSerialize(using = CustomDateSerializer.class)
    private final Date modified;
    private final String title;
    private final String source;

    /**
     * the owner
     */
    private final String userId;
    private final List<String> highlights;
    private final Kind kind;
    private final Long id;

    public SearchHit(SolrDocument solrDocument, Float maxScore, Map<String, List<String>> highlights) {

        this.id = (Long) solrDocument.getFirstValue(SolrFields.DOCUMENT);
        this.modified = (Date) solrDocument.getFirstValue(SolrFields.MODIFIED);
        this.title = (String) solrDocument.getFirstValue(SolrFields.TITLE);
        this.userId = (String) solrDocument.getFirstValue(SolrFields.USER);
        this.source = (String) solrDocument.getFirstValue(SolrFields.SOURCE);
        this.kind = Kind.valueOf((String) solrDocument.getFirstValue(SolrFields.KIND));
        this.score = ((Float) solrDocument.getFirstValue("score") / maxScore); // relative score
        this.highlights = new LinkedList<>();
        if (highlights != null && !highlights.isEmpty()) {
            for (List<String> highlight : highlights.values()) {
                this.highlights.addAll(highlight);
            }
        } else {
            this.highlights.add((String) solrDocument.getFirstValue(SolrFields.OUTLINE));
        }
    }

    public Long getId() {
        return id;
    }

    public Float getScore() {
        return score;
    }

    public Date getModified() {
        return modified;
    }

    public String getTitle() {
        return title;
    }

    public String getUserId() {
        return userId;
    }

    public List<String> getHighlights() {
        return highlights;
    }

    public Kind getKind() {
        return kind;
    }

    public String getSource() {
        return source;
    }
}
