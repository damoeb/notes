package org.notes.core.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.common.ForeignKey;
import org.notes.common.domain.Folder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "User")
@Table(name = "notes_user")
@NamedQueries({
        @NamedQuery(name = User.QUERY_BY_ID, query = "SELECT a FROM User a where a.username=:USERNAME"),
        @NamedQuery(name = User.QUERY_ALL, query = "SELECT a FROM User a")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User implements Serializable {

    public static final String QUERY_BY_ID = "User.QUERY_BY_ID";
    public static final String QUERY_ALL = "User.QUERY_ALL";

    @Id
    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @JsonIgnore
    @Basic
    private String email;

    @JsonIgnore
    @Column(length = 1024)
    private String passwordHash;

//  -- References ------------------------------------------------------------------------------------------------------

    @JsonIgnore
    @OneToMany(cascade = {}, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = ForeignKey.USER)
    private Set<BasicDocument> documents = new HashSet(100);

    @Column(name = Account.FK_ACCOUNT_ID, insertable = false, updatable = false)
    private String accountId;

    @JsonIgnore
    @OneToMany(cascade = {}, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = ForeignKey.USER)
    private Set<StandardDatabase> databases = new HashSet(100);

    @JsonIgnore
    @OneToMany(cascade = {}, fetch = FetchType.LAZY, orphanRemoval = true, targetEntity = StandardFolder.class)
    @JoinColumn(name = ForeignKey.USER)
    private Set<Folder> folders = new HashSet(100);

//  --------------------------------------------------------------------------------------------------------------------

    public User() {
        // default
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<BasicDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<BasicDocument> notes) {
        this.documents = notes;
    }

    public String getAccountId() {
        return accountId;
    }

    protected void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Set<StandardDatabase> getDatabases() {
        return databases;
    }

    public void setDatabases(Set<StandardDatabase> databases) {
        this.databases = databases;
    }

    public Set<Folder> getFolders() {
        return folders;
    }

    public void setFolders(Set<Folder> folders) {
        this.folders = folders;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
