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

    private final Float score;

    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonSerialize(using = CustomDateSerializer.class)
    private final Date modified;
    private final String title;
    private final String owner;
    private final List<String> highlights;
    private final Kind kind;
    private final Boolean star;
    private final String uniqueHash;
    private final Long id;
    private Long numFoundInGroup;
    private final Long section;

    public SearchHit(SolrDocument solrDocument, Map<String, List<String>> highlights) {

        this.id = (Long) solrDocument.getFirstValue(SolrFields.DOCUMENT);
        this.modified = (Date) solrDocument.getFirstValue(SolrFields.MODIFIED);
        if (solrDocument.containsKey(SolrFields.TITLE)) {
            this.title = (String) solrDocument.getFirstValue(SolrFields.TITLE);
        } else {
            this.title = (String) solrDocument.getFirstValue(SolrFields.TITLE_STORED_ONLY);
        }
        this.owner = (String) solrDocument.getFirstValue(SolrFields.OWNER);
        this.star = (Boolean) solrDocument.getFirstValue(SolrFields.STAR);
        this.section = (Long) solrDocument.getFirstValue(SolrFields.SECTION);
        this.uniqueHash = (String) solrDocument.getFirstValue(SolrFields.UNIQUE_HASH);
        this.kind = Kind.valueOf((String) solrDocument.getFirstValue(SolrFields.KIND));
        this.score = (Float) solrDocument.getFirstValue("score");
        this.highlights = new LinkedList<>();
        if (highlights != null) {
            for (List<String> highlight : highlights.values()) {
                this.highlights.addAll(highlight);
            }
        }
    }

    public Long getId() {
        return id;
    }

    public Boolean getStar() {
        return star;
    }

    public String getUniqueHash() {
        return uniqueHash;
    }

    public Float getScore() {
        return score;
    }

    public Date getModified() {
        return modified;
    }

    public Long getSection() {
        return section;
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

    public Long getNumFoundInGroup() {
        return numFoundInGroup;
    }

    public void setNumFoundInGroup(Long numFoundInGroup) {
        this.numFoundInGroup = numFoundInGroup;
    }
}