package org.notes.core.text;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "TermFrequencyProperties")
@Table(name = "TermFrequencyProperties")
@NamedQueries({
        @NamedQuery(name = TermFrequencyProperties.QUERY_BY_NAME, query = "SELECT a FROM TermFrequencyProperties a where a.term=:term")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TermFrequencyProperties implements Serializable {

    public static final String QUERY_BY_NAME = "TermFrequencyProperties.QUERY_BY_NAME";

    @Id
    @Enumerated(EnumType.STRING)
    private TermFrequencyPropertiesKey key;

    @Column(nullable = false)
    private String value;

//  --------------------------------------------------------------------------------------------------------------------

    public TermFrequencyProperties() {
        // default
    }

    public TermFrequencyPropertiesKey getKey() {
        return key;
    }

    public void setKey(TermFrequencyPropertiesKey key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
