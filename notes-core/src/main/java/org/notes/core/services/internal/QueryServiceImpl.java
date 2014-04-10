package org.notes.core.services.internal;

import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.common.services.FolderService;
import org.notes.core.domain.NotesSession;
import org.notes.core.domain.SearchQuery;
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
@TransactionAttribute(TransactionAttributeType.NEVER)
public class QueryServiceImpl implements QueryService {

    private static final Logger LOGGER = Logger.getLogger(QueryServiceImpl.class);

    // --

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private FolderService folderService;

    @Inject
    private NotesSession notesSession;

    // --

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<SearchQuery> history() throws NotesException {
        try {

            if (notesSession == null) {
                throw new IllegalArgumentException("No session data found");
            }

            LOGGER.info("history of user " + notesSession.getUser().getUsername());

            Query query = em.createNamedQuery(SearchQuery.QUERY_LATEST);
            query.setParameter("USERNAME", notesSession.getUser().getUsername());
            query.setMaxResults(10);

            return query.getResultList();

        } catch (Throwable t) {
            String message = String.format("Cannot run history. Reason: %s", t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void log(String queryString) throws NotesException {

        try {

            SearchQuery query;

            try {

                Query findExisting = em.createNamedQuery(SearchQuery.QUERY_BY_QUERY);
                findExisting.setParameter("USERNAME", notesSession.getUser().getUsername());
                findExisting.setParameter("QUERY", queryString);
                query = (SearchQuery) findExisting.getSingleResult();

                query.setLastUsed(new Date());
                query.setUseCount(query.getUseCount() + 1);

                em.merge(query);

            } catch (Exception e) {

                query = new SearchQuery();
                query.setLastUsed(new Date());
                query.setUseCount(1);
                query.setUser(notesSession.getUser());
                query.setValue(queryString);

                em.persist(query);
            }

        } catch (Throwable t) {
            String message = String.format("Cannot run log, queryString=%s. Reason: %s", queryString, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message);
        }
    }
}
