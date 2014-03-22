package org.notes.search.service;

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
import org.notes.common.utils.TextUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NotesInterceptors
public class IndexService {

    private static final Logger LOGGER = Logger.getLogger(IndexService.class);

    @ConfigurationProperty(value = Configuration.SOLR_COMMIT_TIMEOUT, mandatory = true)
    private static final int commitWithinMs = 3000;

    @ConfigurationProperty(value = Configuration.SOLR_SERVER, mandatory = true)
    private String solrUrl = "http://localhost:8080/solr-4.5.1";

    @Inject
    private FolderManager folderManager;

    private HttpSolrServer solrServer;


    public void index(Document document) {

    }

    private void indexTexts(Document document) throws IOException, SolrServerException {

        if (document.getTexts() == null) {
            return;
        }

        Set<SolrInputDocument> docs = new HashSet<>(document.getTexts().size() * 2);
        for (FullText fullText : document.getTexts()) {
            try {

                SolrInputDocument doc = getSolrDocument(document);

                doc.setField(SolrFields.TITLE_STORED_ONLY, document.getTitle());
                doc.setField(SolrFields.SECTION, fullText.getSection());
                doc.setField(SolrFields.TEXT, TextUtils.cleanHtml(fullText.getText()));

                docs.add(doc);

            } catch (NotesException e) {
                LOGGER.error(e.getMessage());
            }
        }

        getSolrServer().add(docs, commitWithinMs);
    }

    private void indexDocument(Document document) throws IOException, SolrServerException, NotesException {
        // todo update document http://wiki.apache.org/solr/UpdateXmlMessages#Optional_attributes_for_.22field.22

        SolrInputDocument doc = getSolrDocument(document);
        doc.setField(SolrFields.TITLE, document.getTitle());

//        todo implement
//        doc.addField(SolrFields.TAG, document.getTagsJson());

        getSolrServer().deleteByQuery(String.format("%s:%s", SolrFields.DOCUMENT, document.getId()));
        getSolrServer().add(doc, commitWithinMs);
    }

    private SolrInputDocument getSolrDocument(Document document) throws NotesException {
        SolrInputDocument doc = new SolrInputDocument();
        doc.setField(SolrFields.DOCUMENT, document.getId());

        List<Folder> parentFolders = folderManager.getParents(document);

        for (Folder parent : parentFolders) {
            doc.setField(SolrFields.FOLDER, parent.getId());
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
        if (solrServer == null) {
            HttpClient httpClient = new DefaultHttpClient();
            solrServer = new HttpSolrServer(solrUrl, httpClient, new XMLResponseParser());
        }
        return solrServer;
    }
}
