package org.notes.core.domain;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Belongs of a database, used to classify documents.
 */
@Entity(name = "SmartSearchFolder")
@Table(name = "SmartSearchFolder")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SmartFolder extends StandardFolder {

    @Basic
    @Column(nullable = false)
    protected String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
