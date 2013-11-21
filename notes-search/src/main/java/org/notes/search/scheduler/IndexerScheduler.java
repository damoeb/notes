package org.notes.search.scheduler;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.common.SolrInputDocument;
import org.notes.common.configuration.Configuration;
import org.notes.common.configuration.ConfigurationProperty;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.model.Document;
import org.notes.common.model.Trigger;
import org.notes.search.interfaces.Indexable;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

//@LocalBean
@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class IndexerScheduler {

    private static final Logger LOGGER = Logger.getLogger(IndexerScheduler.class);

    @ConfigurationProperty(value = Configuration.SOLR_SERVER, mandatory = true, defaultValue = "hase")
    private String solrUrl = "http://localhost:8080/solr-4.5.1";

    @PersistenceContext(unitName = "primary")
    private EntityManager em;


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Lock(LockType.READ)
    @AccessTimeout(value = 3, unit = TimeUnit.SECONDS)
    @Schedule(second = "*/10", minute = "*", hour = "*", persistent = false)
    public void index() {

        try {

            SolrServer server = _getSolrServer();

            // todo get documents where Trigger.DELETE

            Query query = em.createNamedQuery(Document.QUERY_TRIGGER);
            query.setParameter("TRIGGER", Arrays.asList(Trigger.INDEX, Trigger.DELETE));
            List<Document> list = query.getResultList();

            if (!list.isEmpty()) {

                for (Document document : list) {

                    if (Trigger.DELETE == document.getTrigger()) {
                        LOGGER.info("delete " + document.getId());
                    }

                    if (Trigger.INDEX == document.getTrigger()) {
                        LOGGER.info("index " + document.getId());
                    }

                    document.setTrigger(null);
                    em.merge(document);
                    em.flush();

                }
            }


        } catch (Throwable t) {
            LOGGER.error(t);
        }

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
