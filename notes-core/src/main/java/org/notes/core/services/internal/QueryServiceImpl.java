package org.notes.core.services.internal;

import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.common.services.FolderService;
import org.notes.core.domain.SearchQuery;
import org.notes.core.domain.SessionData;
import org.notes.core.services.QueryService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

//@LocalBean
@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class QueryServiceImpl implements QueryService {

    private static final Logger LOGGER = Logger.getLogger(QueryServiceImpl.class);

    // --

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private FolderService folderService;

    @Inject
    private SessionData sessionData;

    // --

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<SearchQuery> history() throws NotesException {
        try {

            if (sessionData == null) {
                throw new IllegalArgumentException("No session data found");
            }

            LOGGER.info("history of user " + sessionData.getUser().getUsername());

            Query query = em.createNamedQuery(SearchQuery.QUERY_LATEST);
            query.setParameter("USERNAME", sessionData.getUser().getUsername());
            query.setMaxResults(10);

            return query.getResultList();

        } catch (Throwable t) {
            String message = String.format("Cannot get query history. Reason: %s", t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void log(String queryString) throws NotesException {

        SearchQuery query;
        try {

            Query findExisting = em.createNamedQuery(SearchQuery.QUERY_BY_QUERY);
            findExisting.setParameter("USERNAME", sessionData.getUser().getUsername());
            findExisting.setParameter("QUERY", queryString);
            query = (SearchQuery) findExisting.getSingleResult();

            query.setLastUsed(new Date());
            query.setUseCount(query.getUseCount() + 1);

            em.merge(query);

        } catch (Throwable t) {

            query = new SearchQuery();
            query.setLastUsed(new Date());
            query.setUseCount(1);
            query.setUser(sessionData.getUser());
            query.setQuery(queryString);

            em.persist(query);
        }
    }

}
