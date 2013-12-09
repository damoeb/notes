package org.notes.search.scheduler;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.common.SolrInputDocument;
import org.notes.common.configuration.Configuration;
import org.notes.common.configuration.ConfigurationProperty;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.interfaces.Document;
import org.notes.common.interfaces.Fulltextable;
import org.notes.common.model.FullText;
import org.notes.common.model.Trigger;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//@LocalBean
@Singleton
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class IndexerScheduler {

    private static final Logger LOGGER = Logger.getLogger(IndexerScheduler.class);
    private static final int COMMIT_WITHIN_MS = 3000;

    @ConfigurationProperty(value = Configuration.SOLR_SERVER, mandatory = true, defaultValue = "hase")
    private String solrUrl = "http://localhost:8080/solr-4.5.1";

    @PersistenceContext(unitName = "primary")
    private EntityManager em;


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Lock(LockType.WRITE)
    @AccessTimeout(0) // no concurrent access
    @Schedule(second = "*/10", minute = "*", hour = "*", persistent = false)
    public void index() {

        try {

            SolrServer server = getSolrServer();

            Query query = em.createNamedQuery(Document.QUERY_TRIGGER);
            query.setParameter("TRIGGER", Arrays.asList(Trigger.INDEX, Trigger.DELETE));
            List<Document> list = query.getResultList();

            if (!list.isEmpty()) {

                for (Document document : list) {

                    if (Trigger.DELETE == document.getTrigger()) {
                        LOGGER.info("delete " + document.getId());
                        server.deleteById(String.valueOf(document.getId()));
                    }

                    if (Trigger.INDEX == document.getTrigger()) {
                        LOGGER.info("index " + document.getId());

                        // standard
                        indexDocument(document);

                        if (document instanceof Fulltextable) {
                            indexFullTexts(document, (Fulltextable) document);
                        }
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

    private void indexFullTexts(Document document, Fulltextable provider) throws IOException, SolrServerException {

        if (provider.getFullTexts() == null) {
            return;
        }

        Set<SolrInputDocument> docs = new HashSet(provider.getFullTexts().size() * 2);
        for (FullText fullText : provider.getFullTexts()) {

            SolrInputDocument doc = new SolrInputDocument();
            doc.setField("document", document.getId());
            doc.setField("folder", document.getFolderId());
            doc.setField("owner", document.getOwnerId());
            doc.setField("text", fullText.getText());
            doc.setField("section", fullText.getSection());
            docs.add(doc);
        }

        getSolrServer().add(docs, COMMIT_WITHIN_MS);
    }

    private void indexDocument(Document document) throws IOException, SolrServerException {
        // todo update document http://wiki.apache.org/solr/UpdateXmlMessages#Optional_attributes_for_.22field.22
        SolrInputDocument doc = new SolrInputDocument();
        //doc.setField("id", document.getId()); // todo fix id
        doc.setField("documentId", document.getId());
        doc.setField("folderId", document.getFolderId());
        doc.setField("modified", document.getModified());
        doc.setField("title", document.getTitle());
        doc.setField("kind", document.getKind());
        doc.setField("ownerId", document.getOwnerId());

        getSolrServer().add(doc, COMMIT_WITHIN_MS);
    }

    private SolrServer getSolrServer() {
        HttpClient httpClient = new DefaultHttpClient();
        SolrServer solr = new HttpSolrServer(solrUrl, httpClient, new XMLResponseParser());
        return solr;
    }
}
