package org.notes.recommend.service;

import org.notes.common.domain.FullText;

import java.util.Collection;
import java.util.Map;

public interface TextEssence {

    Map<String, Double> getBestKeywords(int num, Collection<FullText> texts);

    Double tfidf(String term, Integer frequency, int maxTermFreq);
}
