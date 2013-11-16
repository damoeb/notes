package org.notes.core.dao;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.notes.common.configuration.Configuration;
import org.notes.common.configuration.ConfigurationProperty;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.core.interfaces.SearchManager;
import org.notes.core.model.DocumentHit;
import org.notes.core.model.Kind;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

//@LocalBean
@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class SearchManagerBean implements SearchManager {

    private static final Logger LOGGER = Logger.getLogger(SearchManagerBean.class);

    @ConfigurationProperty(value = Configuration.SOLR_SERVER, mandatory = true)
    private String solrUrl;

    @PersistenceContext(unitName = "primary")
    private EntityManager em;


    @Override
    public List<DocumentHit> query(String query, Long databaseId, Long folderId) throws NotesException {
        try {
            List<DocumentHit> response = new LinkedList<>();

            response.add(new DocumentHit(1d, 1l, new Date(), "Example query result a", "matching highlights", Kind.TEXT));

            HttpClient httpClient = new DefaultHttpClient();
            SolrServer solr = new HttpSolrServer(solrUrl, httpClient, new XMLResponseParser());

            return response;

        } catch (Throwable t) {
            throw new NotesException("query", t);
        }
    }
}
