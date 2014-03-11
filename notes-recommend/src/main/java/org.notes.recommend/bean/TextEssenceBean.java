package org.notes.recommend.bean;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.model.FullText;
import org.notes.common.model.TermFrequency;
import org.notes.common.model.TermFrequencyProperties;
import org.notes.common.model.TermFrequencyPropertiesKey;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

@Stateless
@NotesInterceptors
@TransactionAttribute(TransactionAttributeType.NEVER)
public class TextEssenceBean implements TextEssence {

    private static final Logger LOGGER = Logger.getLogger(TextEssenceBean.class);

    @PersistenceContext(unitName = "primary")
    private EntityManager em;

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Map<String, Double> getBestKeywords(int num, Collection<FullText> texts) {

        final Map<String, Integer> termFreqInDocument = getKeywordFreqMap(texts);

        int maxTermFreq = 0;
        for (Integer freq : termFreqInDocument.values()) {
            if (freq > maxTermFreq) {
                maxTermFreq = freq;
            }
        }

        final int finalMaxTermFreq = maxTermFreq;

        final Map<String, Double> tfidfMap = new HashMap<>(termFreqInDocument.size());

        for (String term : termFreqInDocument.keySet()) {
            Integer freq = termFreqInDocument.get(term);

            try {
                tfidfMap.put(term, tfidf(term, freq, finalMaxTermFreq));
            } catch (IllegalArgumentException e) {
                LOGGER.warn(e.getMessage());
            }
        }

        SortedMap<String, Double> sortedMap = new TreeMap<>(new Comparator<String>() {

            @Override
            public int compare(String s1, String s2) {
                Double tfidf1 = tfidfMap.get(s1);
                Double tfidf2 = tfidfMap.get(s2);
                if (tfidf1.equals(tfidf2)) {
                    return -1;
                }
                return tfidf2.compareTo(tfidf1);
            }
        });

        sortedMap.putAll(tfidfMap);

        String fromKey = sortedMap.firstKey();
        String toKey = sortedMap.lastKey();

        // todo does not work
        int c = 1;
        for (String key : sortedMap.keySet()) {
            if (c++ > num) {
                break;
            }

            toKey = key;
        }

        return sortedMap.subMap(fromKey, toKey);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Double tfidf(String term, Integer frequency, int maxTermFreq) {

        double tf = 0.5 + (0.5 * frequency) / maxTermFreq;

        double N = getTotalDocCount(); // Number Of Documents

        double docsContainingT = (double) getDocCountContainingTerm(term);

        if (docsContainingT <= 0) {
            return 0d;
        }

        double idf = Math.log(N / docsContainingT);

        return tf * idf;
    }

    private Integer getDocCountContainingTerm(String term) {
        try {
            Query query = em.createNamedQuery(TermFrequency.QUERY_BY_TERM);
            query.setParameter("TERM", term.toLowerCase());

            return (Integer) query.getSingleResult();

        } catch (NoResultException e) {
            throw new IllegalArgumentException(String.format("Term '%s' does not exist", term));
        }
    }

    private Double getTotalDocCount() {
        try {
            Query query = em.createNamedQuery(TermFrequencyProperties.QUERY_BY_KEY);
            query.setParameter("KEY", TermFrequencyPropertiesKey.DOCUMENT_COUNT);

            return Double.parseDouble(((TermFrequencyProperties) query.getSingleResult()).getValue());
        } catch (NoResultException e) {

            LOGGER.fatal("Database not properly initialized, cannot find tf property " + TermFrequencyPropertiesKey.DOCUMENT_COUNT.name());
            throw new IllegalStateException("cannot find tf property " + TermFrequencyPropertiesKey.DOCUMENT_COUNT.name(), e);
        }

    }

    private Map<String, Integer> getKeywordFreqMap(Collection<FullText> texts) {

        final Map<String, Integer> keywordFreq = new HashMap<>(300);

        for (FullText text : texts) {

            StringTokenizer tokenizer = new StringTokenizer(text.getText(), " &\\_\"<>|!?=+–­~-*/„“()’`´_#'°^@€%$§[]{}\n\t :,;.ˈ¹−…₂»«%¬”‘·∴ʿ‰″");

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
}
