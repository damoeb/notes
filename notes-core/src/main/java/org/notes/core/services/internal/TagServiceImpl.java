package org.notes.core.services.internal;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.domain.Tag;
import org.notes.common.exceptions.NotesException;
import org.notes.core.domain.BasicDocument;
import org.notes.core.domain.NotesSession;
import org.notes.core.domain.StandardTag;
import org.notes.core.services.TagService;
import org.notes.recommend.service.TextEssence;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//@LocalBean
@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class TagServiceImpl implements TagService {

    private static final Logger LOGGER = Logger.getLogger(TagServiceImpl.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private NotesSession notesSession;

    @Inject
    private TextEssence textEssence;

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public StandardTag findOrCreate(String name) throws NotesException {
        try {
            if (StringUtils.isBlank(name)) {
                throw new IllegalArgumentException("name is null");
            }

            Query query = em.createNamedQuery(StandardTag.QUERY_BY_NAME);
            query.setParameter("NAME", name);
            List results = query.getResultList();
            if (!results.isEmpty()) {
                return (StandardTag) results.get(0);
            }

            StandardTag tag = new StandardTag(name);
            em.persist(tag);
            em.flush();
            em.refresh(tag);

            return tag;

        } catch (Throwable t) {
            String message = String.format("Cannot run findOrCreate, name=%s. Reason: %s", name, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Tag> getRecommendations(BasicDocument document) throws NotesException {
        try {
            if (document == null) {
                throw new IllegalArgumentException("name is null");
            }

            List<Tag> recommendations = new LinkedList<>();

            Map<String, Double> keywords = textEssence.getBestKeywords(10, document.getTexts());

            for (final String kw : keywords.keySet()) {
                recommendations.add(new Tag() {
                    @Override
                    public String getName() {
                        return kw;
                    }
                });
            }

            return recommendations;

        } catch (Throwable t) {
            String message = String.format("Cannot run getRecommendations, document=%s. Reason: %s", document, t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Collection<Tag> getUsersTagNetwork() throws NotesException {
        try {
            Query query = em.createNamedQuery(StandardTag.QUERY_USER_NETWORK);
            query.setMaxResults(5);

            query.setParameter("USERNAME", notesSession.getUserId());
            return query.getResultList();

        } catch (Throwable t) {
            String message = String.format("Cannot run getUsersTagNetwork. Reason: %s", t.getMessage());
            LOGGER.error(message, t);
            throw new NotesException(message, t);
        }
    }
}
