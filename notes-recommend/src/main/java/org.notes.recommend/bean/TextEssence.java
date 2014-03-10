package org.notes.recommend.bean;

import org.notes.common.model.FullText;

import java.util.Collection;
import java.util.Map;

public interface TextEssence {

    Map<String, Double> getBestKeywords(int num, Collection<FullText> texts);

    Double tfidf(String term, Integer frequency, int maxTermFreq);
}
