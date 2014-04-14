package org.notes.core.domain;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.notes.common.configuration.SolrFields;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SearchResponse {

    private final long elapsedTime;
    private long numFound;
    private long start;
    private List<SearchHit> docs;

    public SearchResponse(QueryResponse response) {
        Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();

        SolrDocumentList results = response.getResults();

        numFound = results.getNumFound();
        start = results.getStart();
        elapsedTime = response.getElapsedTime();
        Float maxScore = results.getMaxScore();

        docs = new LinkedList<>();

        for (SolrDocument result : results) {

            String id = (String) result.get(SolrFields.ID);

            SearchHit hit = new SearchHit(result, maxScore, highlighting.get(id));
            docs.add(hit);
        }
    }

    public long getNumFound() {
        return numFound;
    }

    public long getStart() {
        return start;
    }

    public List<SearchHit> getDocs() {
        return docs;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }
}
