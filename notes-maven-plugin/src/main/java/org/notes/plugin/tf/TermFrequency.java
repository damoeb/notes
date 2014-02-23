package org.notes.plugin.tf;

import java.io.Serializable;

class TermFrequency implements Serializable, Comparable<TermFrequency> {

    private String term;
    private int frequency;

    TermFrequency(String term) {
        this.term = term;
        this.frequency = 1;
    }

    public String getTerm() {
        return term;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void incrementFrequency() {
        if (frequency < Integer.MAX_VALUE) {
            frequency++;
        }
    }

    @Override
    public int hashCode() {
        return term.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TermFrequency) {
            getTerm().equals(((TermFrequency) obj).getTerm());
        }
        return false;
    }

    @Override
    public int compareTo(TermFrequency obj) {
        return getTerm().compareTo(obj.getTerm());
    }
}