package org.notes.core.dao;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.common.model.Tag;
import org.notes.core.interfaces.SessionData;
import org.notes.core.interfaces.TagManager;
import org.notes.core.model.BasicDocument;
import org.notes.core.model.DefaultTag;
import org.notes.recommend.bean.TextEssence;

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
public class TagManagerBean implements TagManager {

    private static final Logger LOGGER = Logger.getLogger(TagManagerBean.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Inject
    private SessionData sessionData;

    @Inject
    private TextEssence textEssence;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public DefaultTag findOrCreate(String name) throws NotesException {
        try {
            if (StringUtils.isBlank(name)) {
                throw new NotesException("name is null");
            }

            Query query = em.createNamedQuery(DefaultTag.QUERY_BY_NAME);
            query.setParameter("NAME", name);
            List results = query.getResultList();
            if (!results.isEmpty()) {
                return (DefaultTag) results.get(0);
            }

            DefaultTag tag = new DefaultTag(name);
            em.persist(tag);
            em.flush();
            em.refresh(tag);

            return tag;

        } catch (NotesException e) {
            throw e;
        } catch (Throwable t) {
            throw new NotesException("create folder", t);
        }
    }

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
            throw new NotesException("get recommendations", t);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Collection<Tag> getTagNetwork() throws NotesException {
        try {
            Query query = em.createNamedQuery(DefaultTag.QUERY_USER_NETWORK);
            query.setMaxResults(5);

            query.setParameter("USER", sessionData.getUser().getUsername());
            return query.getResultList();

        } catch (Throwable t) {
            throw new NotesException("get tag-network", t);
        }
    }
}
