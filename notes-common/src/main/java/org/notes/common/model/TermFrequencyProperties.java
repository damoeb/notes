package org.notes.common.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "TermFrequencyProperties")
@Table(name = "TermFrequencyProperties")
@NamedQueries({
        @NamedQuery(name = TermFrequencyProperties.QUERY_BY_KEY, query = "SELECT a FROM TermFrequencyProperties a where a.key=:key")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TermFrequencyProperties implements Serializable {

    public static final String QUERY_BY_KEY = "TermFrequencyProperties.QUERY_BY_KEY";

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "property")
    private TermFrequencyPropertiesKey key;

    @Column(nullable = false, name = "value")
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
