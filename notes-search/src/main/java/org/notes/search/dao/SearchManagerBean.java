package org.notes.search.dao;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.notes.common.configuration.Configuration;
import org.notes.common.configuration.ConfigurationProperty;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.common.model.SolrFields;
import org.notes.search.interfaces.SearchManager;
import org.notes.search.model.DocumentHit;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//@LocalBean
@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class SearchManagerBean implements SearchManager {

    private static final Logger LOGGER = Logger.getLogger(SearchManagerBean.class);

    @ConfigurationProperty(value = Configuration.SOLR_SERVER, mandatory = true)
    private String solrUrl = "http://localhost:8080/solr-4.5.1";

    @Override
    public List<DocumentHit> query(String queryString, int start, int rows) throws NotesException {
        try {
            if (start < 0) {
                start = 0;
            }
            if (rows <= 0 || rows > 100) {
                rows = 100;
            }

            SolrServer server = getSolrServer();

            SolrQuery query = new SolrQuery();

//            todo fix
//            query.setQuery(String.format("+owner:%1$s +(title:%2$%s text:%2$s)", userSessionBean.getUsername(), queryString));
            query.setQuery(String.format("+(title:%2$%s text:%2$s)", queryString));

            query.setFields(SolrFields.ID, SolrFields.DOCUMENT, SolrFields.TITLE, SolrFields.FOLDER, SolrFields.OUTLINE,
                    SolrFields.SECTION, SolrFields.MODIFIED, SolrFields.KIND, SolrFields.OWNER);
            query.setStart(start);
            query.setRows(rows);

            // see http://wiki.apache.org/solr/HighlightingParameters
            query.setHighlightSnippets(1);

            query.addHighlightField(SolrFields.TITLE);
            query.addHighlightField(SolrFields.OUTLINE);
            query.addHighlightField(SolrFields.TEXT);
            query.setIncludeScore(true);

            // todo join http://wiki.apache.org/solr/Join

            // todo facets

            // a hits can be an attachment or folder too

            List<DocumentHit> hits = new LinkedList<>();
            QueryResponse response = server.query(query);
            SolrDocumentList results = response.getResults();
            Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();

            for (SolrDocument solrDocument : results) {
                String id = (String) solrDocument.get(SolrFields.ID);

                hits.add(new DocumentHit(solrDocument, highlighting.get(id)));
            }

            return hits;

        } catch (Throwable t) {
            throw new NotesException("query: " + t.getMessage(), t);
        }
    }

    private SolrServer getSolrServer() {
        HttpClient httpClient = new DefaultHttpClient();
        SolrServer solr = new HttpSolrServer(solrUrl, httpClient, new XMLResponseParser());
        return solr;
    }
}
