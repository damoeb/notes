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
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.notes.common.configuration.Configuration;
import org.notes.common.configuration.ConfigurationProperty;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.configuration.SolrFields;
import org.notes.common.domain.Document;
import org.notes.common.domain.Folder;
import org.notes.common.domain.FullText;
import org.notes.common.exceptions.NotesException;
import org.notes.common.services.FolderService;
import org.notes.common.utils.TextUtils;
import org.notes.core.domain.NotesSession;
import org.notes.core.domain.SearchQuery;
import org.notes.core.domain.SearchResponse;
import org.notes.core.services.SearchService;
import org.notes.core.services.SearchServiceRemote;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

//@LocalBean
@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class SearchServiceImpl implements SearchService, SearchServiceRemote {

    private static final Logger LOGGER = Logger.getLogger(SearchServiceImpl.class);

    @ConfigurationProperty(value = Configuration.SOLR_COMMIT_TIMEOUT, mandatory = true)
    private int commitWithinMs;

    @ConfigurationProperty(value = Configuration.SOLR_SERVER, mandatory = true)
    private String solrUrl;

    // --

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private FolderService folderService;

    @Inject
    private NotesSession notesSession;

    // --

    private HttpSolrServer solrServer;


    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<SearchQuery> getLastQueries() throws NotesException {
        try {

            if (notesSession == null) {
                throw new IllegalArgumentException("No session data found");
            }

            LOGGER.info("getLastQueries of user " + notesSession.getUserId());

            Query query = em.createNamedQuery(SearchQuery.QUERY_LATEST);
            query.setParameter("USERNAME", notesSession.getUserId());
            query.setMaxResults(10);

            return query.getResultList();

        } catch (Throwable t) {
            String message = String.format("Cannot run getLastQueries. Reason: %s", t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void asyncQueryLogging(String queryString) throws NotesException {

        try {

            addLastQueryPersonal(queryString);
            addLastQueryGlobal(queryString);

        } catch (Throwable t) {
            String message = String.format("Cannot run asyncQueryLogging, queryString=%s. Reason: %s", queryString, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SearchResponse find(String queryString, Integer start, Integer rows, Long databaseId, Integer currentFolderId) throws NotesException {
        try {

            if (StringUtils.trim(queryString).length() < 3) {
                throw new IllegalArgumentException("Too short find");
            }

            if (start == null || start < 0) {
                start = 0;
            }
            if (rows == null || rows <= 0 || rows > 30) {
                rows = 30;
            }

            SolrServer server = getSolrServer();

            SolrQuery query = new SolrQuery();

            // todo configure selection of field at one place, not three
            query.setFields(SolrFields.ID, SolrFields.DOCUMENT, SolrFields.TITLE, SolrFields.FOLDER, SolrFields.OUTLINE,
                    SolrFields.MODIFIED, SolrFields.KIND, SolrFields.USER, SolrFields.TEXT, SolrFields.SOURCE);
            query.setStart(start);
            query.setRows(rows);

            // todo exclude trash folder

            // see http://wiki.apache.org/solr/HighlightingParameters
            query.setHighlightSnippets(3);

//            query.addHighlightField(SolrFields.TITLE);
            query.addHighlightField(SolrFields.TEXT);
            query.setIncludeScore(true);

            query.setTimeAllowed(3000);

            // todo filter databaseId
            // todo boost context currentFolderId, if set


            // todo support raw find
            query.setQuery(String.format("+(title:%1$s^5 text:%1$s)", queryString));
            // docs https://wiki.apache.org/solr/FieldCollapsing
//            query.add("group.field", "document");
//            //find.add("group.main", "true");
//            query.add("group", "true");
//            query.add("group.format", "grouped");

            // todo facets

            // a hits can be an attachment

            QueryResponse response = server.query(query);

            return new SearchResponse(response);

        } catch (Throwable t) {
            throw new NotesException("search aborted: " + t.getMessage(), t);
        }
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<SearchQuery> getQuerySuggestions(String queryString) throws NotesException {
        try {

            SolrQuery query = getSuggestionQuery(queryString);
            query.setStart(0);
            query.setRows(10);
            query.setSort(SolrFields.QUERY_SCORE, SolrQuery.ORDER.desc);

            SolrServer server = getSolrServer();
            QueryResponse response = server.query(query);
            SolrDocumentList results = response.getResults();

            List<SearchQuery> suggestions = new LinkedList<>();
            for (SolrDocument document : results) {
                suggestions.add(new SearchQuery(document));
            }

            return suggestions;

        } catch (Throwable t) {
            throw new NotesException("find suggestion aborted: " + t.getMessage(), t);
        }
    }

    private SolrQuery getSuggestionQuery(String queryString) {
        SolrQuery query = new SolrQuery("query:" + queryString);
        query.setFields(SolrFields.ID, SolrFields.QUERY, SolrFields.QUERY_SCORE);
        return query;
    }

    /**
     * {@inheritDoc}
     * @param documents
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void index(Collection<Document> documents) throws NotesException {
        try {

            if (documents == null) {
                throw new IllegalArgumentException("Document is null");
            }

            for (Document document : documents) {

                LOGGER.info("index document #" + document.getId());

                SolrInputDocument solrDocument = getSolrDocument(document);

                indexDocument(solrDocument, document);
                indexAdditionalTexts(solrDocument, document);

            }
        } catch (Throwable t) {
            String message = String.format("Cannot index document %s. Reason: %s", documents, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteFromIndex(Collection<Document> documents) throws NotesException {
        try {

            if (documents == null) {
                throw new IllegalArgumentException("Document is null");
            }

            for (Document document : documents) {

                LOGGER.info("de-index document #" + document.getId());
                // todo implement
            }

        } catch (Throwable t) {
            String message = String.format("Cannot index document %s. Reason: %s", documents, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message);
        }
    }

    // -- Internal

    private void addLastQueryPersonal(String queryString) {

        // todo improve
        SearchQuery query;

        try {

            Query findExisting = em.createNamedQuery(SearchQuery.QUERY_BY_QUERY);
            findExisting.setParameter("USERNAME", notesSession.getUserId());
            findExisting.setParameter("QUERY", queryString);
            query = (SearchQuery) findExisting.getSingleResult();

            query.setLastUsed(new Date());
            query.setUseCount(query.getUseCount() + 1);
//            query.setUserId(notesSession.getUserId());

            em.merge(query);

        } catch (Exception e) {

            query = new SearchQuery();
            query.setLastUsed(new Date());
            query.setUseCount(1);
            query.setUserId(notesSession.getUserId());
            query.setValue(queryString);

            em.persist(query);
        }

    }

    private void addLastQueryGlobal(String queryString) throws SolrServerException, IOException {

        // todo should be done via logs

        SolrQuery query = getSuggestionQuery(queryString);
        query.setStart(0);
        query.setRows(1);

        SolrServer server = getSolrServer();
        QueryResponse response = server.query(query);
        SolrDocumentList results = response.getResults();

        if (results.getNumFound() == 0) {

            SolrInputDocument input = new SolrInputDocument();
            input.addField(SolrFields.QUERY, queryString);
            input.addField(SolrFields.QUERY_SCORE, 1);
            server.add(input);

        } else {
//            SolrDocument existing = results.get(0);
            // todo update use count
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
        // jeder tag einzelln, fuer facettierung

        getSolrServer().deleteByQuery(String.format("%s:%s", SolrFields.DOCUMENT, document.getId()));
        getSolrServer().add(solrDocument, commitWithinMs);
    }

    private void indexAdditionalTexts(SolrInputDocument solrDocument, Document document) throws IOException, SolrServerException, NotesException {

        if (document.getTexts() == null) {
            return;
        }

        // todo use hierarchical documents
//        if (document.getTexts().size() <= 1) {
//            return;
//        }
//
//        Set<SolrInputDocument> docs = new HashSet<>(document.getTexts().size() * 2);
//        for (FullText fullText : document.getTexts()) {
//
//            solrDocument.setField(SolrFields.TITLE_STORED_ONLY, document.getTitle());
//            solrDocument.setField(SolrFields.SECTION, fullText.getSection());
//            solrDocument.setField(SolrFields.TEXT, TextUtils.cleanHtml(fullText.getText()));
//
//            docs.add(solrDocument);
//
//        }

//        getSolrServer().add(docs, commitWithinMs);
    }

    private SolrInputDocument getSolrDocument(Document document) throws NotesException {
        SolrInputDocument doc = new SolrInputDocument();
        doc.setField(SolrFields.DOCUMENT, document.getId());
        doc.setField(SolrFields.SOURCE, document.getSource());
        doc.setField(SolrFields.MODIFIED, document.getModified());
        doc.setField(SolrFields.OUTLINE, document.getOutline());
        doc.setField(SolrFields.KIND, document.getKind());
        doc.setField(SolrFields.USER, document.getUserId());

        List<Folder> parentFolders = folderService.getParents(document);

        for (Folder parent : parentFolders) {
            doc.setField(SolrFields.FOLDER, parent.getId());
        }

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
