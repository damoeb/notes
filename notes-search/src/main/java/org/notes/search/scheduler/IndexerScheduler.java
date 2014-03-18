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
import org.notes.common.configuration.SolrFields;
import org.notes.common.exceptions.NotesException;
import org.notes.common.interfaces.Document;
import org.notes.common.interfaces.FolderManager;
import org.notes.common.model.Folder;
import org.notes.common.model.FullText;
import org.notes.common.model.Trigger;
import org.notes.common.utils.TextUtils;

import javax.ejb.*;
import javax.inject.Inject;
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

    @ConfigurationProperty(value = Configuration.SOLR_SERVER, mandatory = true)
    private String solrUrl = "http://localhost:8080/solr-4.5.1";

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private FolderManager folderManager;

    private HttpSolrServer solr;


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

                        indexDocument(document);

                        indexTexts(document);
                    }

                    document.setTrigger(Trigger.ESSENCE);
                    em.merge(document);
                    em.flush();

                }
            }

        } catch (Throwable t) {
            LOGGER.error(t);
        }
    }

    private void indexTexts(Document document) throws IOException, SolrServerException {

        if (document.getTexts() == null) {
            return;
        }

        Set<SolrInputDocument> docs = new HashSet<>(document.getTexts().size() * 2);
        for (FullText fullText : document.getTexts()) {

            SolrInputDocument doc = getSolrDocument(document);

            doc.setField(SolrFields.TITLE_STORED_ONLY, document.getTitle());
            doc.setField(SolrFields.SECTION, fullText.getSection());
            doc.setField(SolrFields.TEXT, TextUtils.cleanHtml(fullText.getText()));

            docs.add(doc);
        }

        getSolrServer().add(docs, COMMIT_WITHIN_MS);
    }

    private void indexDocument(Document document) throws IOException, SolrServerException {
        // todo update document http://wiki.apache.org/solr/UpdateXmlMessages#Optional_attributes_for_.22field.22

        SolrInputDocument doc = getSolrDocument(document);
        doc.setField(SolrFields.TITLE, document.getTitle());

//        todo implement
//        doc.addField(SolrFields.TAG, document.getTagsJson());

        getSolrServer().deleteByQuery(String.format("%s:%s", SolrFields.DOCUMENT, document.getId()));
        getSolrServer().add(doc, COMMIT_WITHIN_MS);
    }

    private SolrInputDocument getSolrDocument(Document document) {
        SolrInputDocument doc = new SolrInputDocument();
        doc.setField(SolrFields.DOCUMENT, document.getId());

        // index containing folder nodes incl its parents
        Long folderId = document.getFolderId();
        try {
            while (folderId != null) {
                doc.setField(SolrFields.FOLDER, folderId);

                Folder parent = folderManager.getFolder(folderId);
                folderId = parent.getId();
            }
        } catch (NotesException e) {
            LOGGER.error(e);
        }

        doc.setField(SolrFields.MODIFIED, document.getModified());
        doc.setField(SolrFields.OUTLINE, document.getOutline());
        doc.setField(SolrFields.KIND, document.getKind());
        doc.setField(SolrFields.OWNER, document.getOwner());
        doc.setField(SolrFields.UNIQUE_HASH, document.getUniqueHash());
        doc.setField(SolrFields.STAR, document.isStar());
        return doc;
    }

    private SolrServer getSolrServer() {
        if (solr == null) {
            HttpClient httpClient = new DefaultHttpClient();
            solr = new HttpSolrServer(solrUrl, httpClient, new XMLResponseParser());
        }
        return solr;
    }
}
