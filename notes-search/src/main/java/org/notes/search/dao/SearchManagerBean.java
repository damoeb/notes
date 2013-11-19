package org.notes.search.dao;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.common.SolrInputDocument;
import org.notes.common.configuration.Configuration;
import org.notes.common.configuration.ConfigurationProperty;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.common.model.Kind;
import org.notes.search.interfaces.Indexable;
import org.notes.search.interfaces.SearchManager;
import org.notes.search.model.DocumentHit;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

//@LocalBean
@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class SearchManagerBean implements SearchManager {

    private static final Logger LOGGER = Logger.getLogger(SearchManagerBean.class);

    @ConfigurationProperty(value = Configuration.SOLR_SERVER, mandatory = true, defaultValue = "hase")
    private String solrUrl = "http://localhost:8080/solr-4.5.1";

    @PersistenceContext(unitName = "primary")
    private EntityManager em;


    @Override
    public List<DocumentHit> query(String queryString, int start, int rows) throws NotesException {
        try {

            SolrServer server = _getSolrServer();

            SolrQuery query = new SolrQuery();
            query.setQuery(queryString);
            query.addFilterQuery("userId:1", "store:amazon.com");
            query.setFields("id", "title", "outline", "modified", "kind");
            query.setStart(0);
            query.setRows(100);

            // todo join http://wiki.apache.org/solr/Join

            // todo facets

            return Arrays.asList(new DocumentHit(1d, 1l, new Date(), "Example query result a", "matching highlights", Kind.TEXT));

        } catch (Throwable t) {
            throw new NotesException("query: " + t.getMessage(), t);
        }
    }

    @Override
    public void index(Indexable indexable) throws NotesException {
        try {

            SolrServer server = _getSolrServer();

            server.add(_toSolrDocument(indexable), 4000);

        } catch (Throwable t) {
            throw new NotesException("index: " + t.getMessage(), t);
        }
    }

    @Override
    public void delete(Indexable indexable) {

    }

    private SolrInputDocument _toSolrDocument(Indexable indexable) {
        // todo update document http://wiki.apache.org/solr/UpdateXmlMessages#Optional_attributes_for_.22field.22
        SolrInputDocument document = new SolrInputDocument();
        document.setField("title", indexable.getTitle());
        return document;
    }

    private SolrServer _getSolrServer() {
        HttpClient httpClient = new DefaultHttpClient();
        SolrServer solr = new HttpSolrServer(solrUrl, httpClient, new XMLResponseParser());
        return solr;
    }
}
