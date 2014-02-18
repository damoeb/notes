package org.notes.core.dao;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.common.model.FullText;
import org.notes.common.model.Tag;
import org.notes.core.interfaces.SessionData;
import org.notes.core.interfaces.TagManager;
import org.notes.core.model.BasicDocument;
import org.notes.core.model.DefaultTag;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

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

            Collection<String> keywords = getBestKeywords(10, document.getTexts());

            for (final String kw : keywords) {
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

    private Set<String> getBestKeywords(int num, Collection<FullText> texts) {

        final Map<String, Integer> keywordFreq = getKeywordFreqMap(texts);

        // todo calc tf-idf

        SortedSet<String> byScore = new TreeSet<>(new Comparator<String>() {

            @Override
            public int compare(String s1, String s2) {
                Integer f1 = keywordFreq.get(s1);
                Integer f2 = keywordFreq.get(s2);
                if (f1.equals(f2)) {
                    return -1;
                }
                return f2.compareTo(f1);
            }
        });

        byScore.addAll(keywordFreq.keySet());

        // order by name
        SortedSet<String> byName = new TreeSet<>(new Comparator<String>() {

            @Override
            public int compare(String s1, String s2) {
                return s2.compareTo(s1);
            }
        });

        for (String kw : byScore) {

            byName.add(kw);

            if (byName.size() >= num) {
                break;
            }
        }

        return byName;
    }

    private Map<String, Integer> getKeywordFreqMap(Collection<FullText> texts) {

        final Map<String, Integer> keywordFreq = new HashMap<>(300);

        for (FullText text : texts) {

            StringTokenizer tokenizer = new StringTokenizer(text.getText(), " .,;:-+*?!'^\"/\\&<>()[]{}\n\t\r");

            while (tokenizer.hasMoreTokens()) {
                String keyword = tokenizer.nextToken();
                // filter
                if (!isKeyword(keyword)) {
                    continue;
                }
                // stem
                keyword = stem(keyword);

                // stop words
                if (isStopWord(keyword)) {
                    continue;
                }

                if (!keywordFreq.containsKey(keyword)) {
                    keywordFreq.put(keyword, 0);
                }
                keywordFreq.put(keyword, keywordFreq.get(keyword) + 1);
            }
        }

        return keywordFreq;
    }

    private boolean isStopWord(String keyword) {
        // todo use stop words
        return false;
    }

    private String stem(String keyword) {
        // todo implement stem
        return keyword;
    }

    private boolean isKeyword(String token) {
        return StringUtils.length(StringUtils.trim(token)) > 2;
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
