package org.notes.plugin.tf;

import org.notes.common.model.TermFrequency;

import java.util.SortedSet;

public class ParserResult {

    private final long documentCount;
    private final SortedSet<TermFrequency> terms;

    public ParserResult(long documentCount, SortedSet<TermFrequency> terms) {
        this.documentCount = documentCount;
        this.terms = terms;
    }

    public long getDocumentCount() {
        return documentCount;
    }

    public SortedSet<TermFrequency> getTerms() {
        return terms;
    }
}
