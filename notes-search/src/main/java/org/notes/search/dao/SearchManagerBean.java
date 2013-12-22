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
import org.notes.common.model.IndexFields;
import org.notes.search.interfaces.SearchManager;
import org.notes.search.model.DocumentHit;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.LinkedList;
import java.util.List;

//@LocalBean
@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class SearchManagerBean implements SearchManager {

    private static final Logger LOGGER = Logger.getLogger(SearchManagerBean.class);

    @ConfigurationProperty(value = Configuration.SOLR_SERVER, mandatory = true, defaultValue = "hase")
    private String solrUrl = "http://localhost:8080/solr-4.5.1";

    @Override
    public List<DocumentHit> query(String queryString, int start, int rows) throws NotesException {
        try {

            SolrServer server = getSolrServer();

            SolrQuery query = new SolrQuery();
            query.setQuery(queryString);
            //query.addFilterQuery("owner:1", "store:amazon.com");
            query.setFields(IndexFields.DOCUMENT, IndexFields.TITLE, IndexFields.FOLDER, IndexFields.OUTLINE, IndexFields.SECTION, IndexFields.MODIFIED, IndexFields.KIND);
            query.setStart(0);
            query.setRows(100);

            // todo join http://wiki.apache.org/solr/Join

            // todo facets


            List<DocumentHit> hits = new LinkedList<>();
            QueryResponse response = server.query(query);
            SolrDocumentList results = response.getResults();
            for (SolrDocument solrDocument : results) {
                // todo set highlights, score
                hits.add(new DocumentHit(solrDocument));
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
