package org.notes.core.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.common.ForeignKey;
import org.notes.common.model.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "User")
@Table(name = "User")
@NamedQueries({
        @NamedQuery(name = User.QUERY_BY_ID, query = "SELECT a FROM User a where a.id=:ID"),
        @NamedQuery(name = User.QUERY_BY_USERNAME, query = "SELECT a FROM User a where LOWER(a.username)=LOWER(:USERNAME)"),
        @NamedQuery(name = User.QUERY_ALL, query = "SELECT a FROM User a")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User implements Serializable {

    public static final String QUERY_BY_ID = "User.QUERY_BY_ID";
    public static final String QUERY_BY_USERNAME = "User.QUERY_BY_USERNAME";
    public static final String QUERY_ALL = "User.QUERY_ALL";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Basic
    @Column(nullable = false, unique = true)
    private String username;

//  -- References ------------------------------------------------------------------------------------------------------

    @JsonIgnore
    @OneToMany(cascade = {}, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = ForeignKey.OWNER_ID)
    private Set<Document> documents = new HashSet(100);

    @Column(name = Account.FK_ACCOUNT_ID, insertable = false, updatable = false)
    private Long accountId;

    @JsonIgnore
    @OneToMany(cascade = {}, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = ForeignKey.OWNER_ID)
    private Set<Database> databases = new HashSet(100);

    @JsonIgnore
    @OneToMany(cascade = {}, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = ForeignKey.OWNER_ID)
    private Set<Folder> folders = new HashSet(100);

//  --------------------------------------------------------------------------------------------------------------------

    public User() {
        // default
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<Document> notes) {
        this.documents = notes;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Set<Database> getDatabases() {
        return databases;
    }

    public void setDatabases(Set<Database> databases) {
        this.databases = databases;
    }

    public Set<Folder> getFolders() {
        return folders;
    }

    public void setFolders(Set<Folder> folders) {
        this.folders = folders;
    }
}
