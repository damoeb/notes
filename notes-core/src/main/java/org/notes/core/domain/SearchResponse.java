package org.notes.core.domain;

import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
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

        List<GroupCommand> groupCommandList = response.getGroupResponse().getValues();

        docs = new LinkedList<>();

        for (GroupCommand groupCommand : groupCommandList) {
            this.numFound = groupCommand.getMatches();

            for (Group group : groupCommand.getValues()) {

                SolrDocumentList list = group.getResult();
                for (SolrDocument solrDocument : list) {
                    String id = (String) solrDocument.get(SolrFields.ID);

                    start = list.getStart();

                    SearchHit hit = new SearchHit(solrDocument, highlighting.get(id));
                    hit.setNumFoundInGroup(list.getNumFound());
                    docs.add(hit);
                }
            }
        }

//        numFound = results.getNumFound();
//        start = results.getStart();
        elapsedTime = response.getElapsedTime();
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