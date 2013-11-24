package org.notes.search.text;

import java.util.List;

public class ExtractionResult {
    private List<String> fullTexts;
    private int numberOfPages;

    public ExtractionResult(List<String> fullTexts, int numberOfPages) {
        this.fullTexts = fullTexts;
        this.numberOfPages = numberOfPages;
    }

    public List<String> getFullTexts() {
        return fullTexts;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }
}
