package org.notes.core.services.internal;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.notes.common.configuration.Configuration;
import org.notes.common.configuration.ConfigurationProperty;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.configuration.SolrFields;
import org.notes.common.domain.Document;
import org.notes.common.domain.FullText;
import org.notes.common.exceptions.NotesException;
import org.notes.common.services.FolderService;
import org.notes.common.utils.TextUtils;
import org.notes.core.domain.SearchResponse;
import org.notes.core.domain.SessionData;
import org.notes.core.services.QueryService;
import org.notes.core.services.SearchService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

//@LocalBean
@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class SolrServiceImpl implements SearchService {

    private static final Logger LOGGER = Logger.getLogger(SolrServiceImpl.class);

    @ConfigurationProperty(value = Configuration.SOLR_COMMIT_TIMEOUT, mandatory = true)
    private int commitWithinMs;

    @ConfigurationProperty(value = Configuration.SOLR_SERVER, mandatory = true)
    private String solrUrl;

    // --

    @Inject
    private FolderService folderService;

    @Inject
    private QueryService queryService;

    @Inject
    private SessionData sessionData;

    // --

    private HttpSolrServer solrServer;

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResponse query(String queryString, Integer start, Integer rows, Long databaseId, Integer currentFolderId) throws NotesException {
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

            queryService.log(queryString);

            SolrServer server = getSolrServer();

            SolrQuery query = new SolrQuery();

            query.setFields(SolrFields.ID, SolrFields.DOCUMENT, SolrFields.TITLE, SolrFields.TITLE_STORED_ONLY, SolrFields.FOLDER, SolrFields.OUTLINE,
                    SolrFields.SECTION, SolrFields.MODIFIED, SolrFields.KIND, SolrFields.OWNER, SolrFields.STAR,
                    SolrFields.UNIQUE_HASH, SolrFields.TEXT);
            query.setStart(start);
            query.setRows(rows);

            // todo exclude trash folder

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

            // a hits can be an attachment

            QueryResponse response = server.query(query);

            return new SearchResponse(response);

        } catch (Throwable t) {
            throw new NotesException("search aborted: " + t.getMessage(), t);
        }
    }


    /**
     * {@inheritDoc}
     */
    public void index(Document document) throws NotesException {
        try {

            if (document == null) {
                throw new IllegalArgumentException("Document is null");
            }

            LOGGER.info("index document #" + document.getId());

            SolrInputDocument solrDocument = getSolrDocument(document);

            indexDocument(solrDocument, document);
            indexAdditionalTexts(solrDocument, document);

        } catch (Throwable t) {
            String message = String.format("Cannot index document %s. Reason: %s", document, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message);
        }
    }

    private void indexDocument(SolrInputDocument solrDocument, Document document) throws IOException, SolrServerException, NotesException {
        // todo update document http://wiki.apache.org/solr/UpdateXmlMessages#Optional_attributes_for_.22field.22

        solrDocument.setField(SolrFields.TITLE, document.getTitle());

        if (document.getTexts() != null && document.getTexts().size() == 1) {
            FullText firstText = document.getTexts().iterator().next();
            solrDocument.setField(SolrFields.TEXT, TextUtils.cleanHtml(firstText.getText()));
        }

//        todo implement
//        doc.addField(SolrFields.TAG, document.getTagsJson());

        getSolrServer().deleteByQuery(String.format("%s:%s", SolrFields.DOCUMENT, document.getId()));
        getSolrServer().add(solrDocument, commitWithinMs);
    }

    private void indexAdditionalTexts(SolrInputDocument solrDocument, Document document) throws IOException, SolrServerException, NotesException {

        if (document.getTexts() == null) {
            return;
        }

        if (document.getTexts().size() <= 1) {
            return;
        }

        Set<SolrInputDocument> docs = new HashSet<>(document.getTexts().size() * 2);
        for (FullText fullText : document.getTexts()) {

            solrDocument.setField(SolrFields.TITLE_STORED_ONLY, document.getTitle());
            solrDocument.setField(SolrFields.SECTION, fullText.getSection());
            solrDocument.setField(SolrFields.TEXT, TextUtils.cleanHtml(fullText.getText()));

            docs.add(solrDocument);

        }

        getSolrServer().add(docs, commitWithinMs);
    }

    private SolrInputDocument getSolrDocument(Document document) throws NotesException {
        SolrInputDocument doc = new SolrInputDocument();
        doc.setField(SolrFields.DOCUMENT, document.getId());

//        todo implement
//        List<Folder> parentFolders = folderService.getParents(document);
//
//        for (Folder parent : parentFolders) {
//            doc.setField(SolrFields.FOLDER, parent.getId());
//        }

        doc.setField(SolrFields.MODIFIED, document.getModified());
        doc.setField(SolrFields.OUTLINE, document.getOutline());
        doc.setField(SolrFields.KIND, document.getKind());
        doc.setField(SolrFields.OWNER, document.getOwner());
        doc.setField(SolrFields.UNIQUE_HASH, document.getUniqueHash());
        String url = document.getUrl();
        if (StringUtils.isNotBlank(url)) {
            try {
                doc.setField(SolrFields.DOMAIN, new URL(url).getHost());
            } catch (MalformedURLException e) {
                LOGGER.warn(String.format("url %s is invalid.", url));
            }
        }
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
