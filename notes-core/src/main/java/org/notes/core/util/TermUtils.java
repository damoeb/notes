package org.notes.core.util;

public final class TermUtils {

    private TermUtils() {
    }

    public static Double tfidf(String term, Integer frequency, int maxTermFreq) {

        double tf = 0.5 + (0.5 * frequency) / maxTermFreq;

        double N = 0; // Number Of Documents

        double docsContainingT = 2;

        double idf = Math.log(N / docsContainingT);

        return tf * idf;
    }
}
