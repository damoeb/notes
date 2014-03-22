package org.notes.search.dao;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.notes.common.configuration.Configuration;
import org.notes.common.configuration.ConfigurationProperty;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.configuration.SolrFields;
import org.notes.common.exceptions.NotesException;
import org.notes.search.interfaces.SearchManager;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

//@LocalBean
@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class SolrProxyBean implements SearchManager {

    private static final Logger LOGGER = Logger.getLogger(SolrProxyBean.class);

    @ConfigurationProperty(value = Configuration.SOLR_SERVER, mandatory = true)
    private String solrUrl = "http://localhost:8080/solr-4.5.1";

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResponse query(String queryString, Integer start, Integer rows, Long databaseId, Integer currentFolderId, Boolean contextOnly) throws NotesException {
        try {

            if (StringUtils.trim(queryString).length() < 3) {
                throw new IllegalArgumentException("Too short query");
            }

            if (start == null || start < 0) {
                start = 0;
            }
            if (rows == null || rows <= 0 || rows > 100) {
                rows = 100;
            }

            SolrServer server = getSolrServer();

            SolrQuery query = new SolrQuery();

            query.setFields(SolrFields.ID, SolrFields.DOCUMENT, SolrFields.TITLE, SolrFields.TITLE_STORED_ONLY, SolrFields.FOLDER, SolrFields.OUTLINE,
                    SolrFields.SECTION, SolrFields.MODIFIED, SolrFields.KIND, SolrFields.OWNER, SolrFields.STAR,
                    SolrFields.UNIQUE_HASH, SolrFields.TEXT);
            query.setStart(start);
            query.setRows(rows);

            // see http://wiki.apache.org/solr/HighlightingParameters
            query.setHighlightSnippets(3);

            query.addHighlightField(SolrFields.TITLE);
            query.addHighlightField(SolrFields.TEXT);
            query.setIncludeScore(true);

            query.setTimeAllowed(3000);

            // todo filter databaseId
            // todo boost context currentFolderId, if set


            // todo support raw query
            query.setQuery(String.format("+(title:%1$s^10 text:%1$s)", queryString));
            // docs https://wiki.apache.org/solr/FieldCollapsing
            query.add("group.field", "document");
            //query.add("group.main", "true");
            query.add("group", "true");
            query.add("group.format", "grouped");

            // todo facets

            // a hits can be an attachment or folder too

            QueryResponse response = server.query(query);

            return new SearchResponse(response);

        } catch (Throwable t) {
            throw new NotesException("search aborted: " + t.getMessage(), t);
        }
    }

    private SolrServer getSolrServer() {
        HttpClient httpClient = new DefaultHttpClient();
        SolrServer solr = new HttpSolrServer(solrUrl, httpClient, new XMLResponseParser());
        return solr;
    }
}
