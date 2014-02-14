package org.notes.search.dao;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.notes.common.model.SolrFields;
import org.notes.search.model.DocumentHit;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SearchResponse {

    private long numFound;
    private long start;
    private List<DocumentHit> docs;

    public SearchResponse(QueryResponse response) {
        SolrDocumentList results = response.getResults();

        Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();

        docs = new LinkedList<>();

        for (SolrDocument solrDocument : results) {
            String id = (String) solrDocument.get(SolrFields.ID);

            docs.add(new DocumentHit(solrDocument, highlighting.get(id)));
        }

        numFound = results.getNumFound();
        start = results.getStart();
    }

    public long getNumFound() {
        return numFound;
    }

    public long getStart() {
        return start;
    }

    public List<DocumentHit> getDocs() {
        return docs;
    }
}
