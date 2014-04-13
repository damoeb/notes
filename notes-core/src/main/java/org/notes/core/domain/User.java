package org.notes.core.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.common.ForeignKey;
import org.notes.common.domain.Folder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
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

    @JsonIgnore
    @Column(length = 256)
    private String salt;

    @Basic
    private int documentCount;

    @Basic
    private int folderCount;

//  -- Security

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;

    @Basic
    private boolean deactivated;

    @Basic
    private int loginTries;


//  -- References ------------------------------------------------------------------------------------------------------

    @JsonIgnore
    @OneToMany(cascade = {}, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = ForeignKey.USER_ID)
    private Set<BasicDocument> documents = new HashSet(100);

    @Column(name = Account.FK_ACCOUNT_ID, insertable = false, updatable = false)
    private int accountId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Account.FK_ACCOUNT_ID)
    private Account account;

    @JsonIgnore
    @OneToMany(cascade = {}, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = ForeignKey.USER_ID)
    private Set<StandardDatabase> databases = new HashSet(100);

    @JsonIgnore
    @OneToMany(cascade = {}, fetch = FetchType.LAZY, orphanRemoval = true, targetEntity = StandardFolder.class)
    @JoinColumn(name = ForeignKey.USER_ID)
    private Set<Folder> folders = new HashSet(100);

//  --------------------------------------------------------------------------------------------------------------------

    public User() {
        // default
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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

    public int getAccountId() {
        return accountId;
    }

    protected void setAccountId(int accountId) {
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

    public int getDocumentCount() {
        return documentCount;
    }

    public void setDocumentCount(int documentCount) {
        this.documentCount = documentCount;
    }

    public int getFolderCount() {
        return folderCount;
    }

    public void setFolderCount(int folderCount) {
        this.folderCount = folderCount;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }

    public int getLoginTries() {
        return loginTries;
    }

    public void setLoginTries(int loginTries) {
        this.loginTries = loginTries;
    }
}
