package org.notes.search;

import java.util.Map;

public class ExtractionResult {

    private Map<Integer, String> fullTexts;
    private int numberOfPages;

    public ExtractionResult(Map<Integer, String> fullTexts, int numberOfPages) {
        this.fullTexts = fullTexts;
        this.numberOfPages = numberOfPages;
    }

    public Map<Integer, String> getFullTexts() {
        return fullTexts;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }
}
