package org.notes.core.domain;

import org.apache.solr.common.SolrDocument;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.common.ForeignKey;
import org.notes.common.configuration.SolrFields;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "SearchQuery")
@Table(name = "SearchQuery")
@NamedQueries({
        @NamedQuery(name = SearchQuery.QUERY_LATEST, query = "SELECT a FROM SearchQuery a where a.username=:USERNAME"),
        @NamedQuery(name = SearchQuery.QUERY_BY_QUERY, query = "SELECT a FROM SearchQuery a where a.username=:USERNAME AND a.value=:QUERY")
})
@Inheritance(strategy = InheritanceType.JOINED)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchQuery {

    public static final String QUERY_LATEST = "SearchQuery.QUERY_LATEST"; // todo limit rows to 100
    public static final String QUERY_BY_QUERY = "SearchQuery.QUERY_BY_QUERY";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private int useCount;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUsed;

//  -- References ------------------------------------------------------------------------------------------------------

    @JsonIgnore
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = ForeignKey.USER)
    private User user;


    @Column(name = ForeignKey.USER, insertable = false, updatable = false, nullable = false)
    private String username;

//  --------------------------------------------------------------------------------------------------------------------

    public SearchQuery() {
        //
    }

    public SearchQuery(SolrDocument document) {
        this.value = (String) document.getFieldValue(SolrFields.QUERY);
        // todo more fields
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getUseCount() {
        return useCount;
    }

    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
