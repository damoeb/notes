package org.notes.core.text;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "TermFrequency")
@Table(name = "TermFrequency")
@NamedQueries({
        @NamedQuery(name = TermFrequency.QUERY_BY_NAME, query = "SELECT a FROM TermFrequency a where a.term=:term")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TermFrequency implements Serializable {

    public static final String QUERY_BY_NAME = "TermFrequency.QUERY_BY_NAME";

    @Id
    @Index(name = "termIdx")
    private String term;

    @Basic
    private int frequency;

//  --------------------------------------------------------------------------------------------------------------------

    public TermFrequency() {
        // default
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
