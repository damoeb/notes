package org.notes.text;

import org.notes.common.model.FullText;

import java.util.Set;

public class ExtractionResult {

    private Set<FullText> fullTexts;
    private int numberOfPages;

    public ExtractionResult(Set<FullText> fullTexts, int numberOfPages) {
        this.fullTexts = fullTexts;
        this.numberOfPages = numberOfPages;
    }

    public Set<FullText> getFullTexts() {
        return fullTexts;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }
}
