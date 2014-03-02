package org.notes.common.model;

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
    private Integer frequency;

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

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TermFrequency)) return false;

        TermFrequency that = (TermFrequency) o;

        if (!term.equalsIgnoreCase(that.term)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return term.hashCode();
    }
}
