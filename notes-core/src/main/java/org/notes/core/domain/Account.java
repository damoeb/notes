package org.notes.core.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Every <code>user</code> is associated to an account, that specifies general settings like quota
 */
@Entity(name = "Account")
@Table(name = "Account")
@NamedQueries({
        @NamedQuery(name = Account.QUERY_BY_TYPE, query = "SELECT a FROM Account a where a.type=:TYPE"),
        @NamedQuery(name = Account.QUERY_ALL, query = "SELECT a FROM Account a")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Account implements Serializable {

    public static final String QUERY_BY_TYPE = "Account.QUERY_BY_TYPE";
    public static final String QUERY_ALL = "Account.QUERY_ALL";
    public static final String FK_ACCOUNT_ID = "account_id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Basic
    private int documentCount;

    @Basic
    private int folderCount;

//  -- References ------------------------------------------------------------------------------------------------------

    @JsonIgnore
    @OneToMany(cascade = {}, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = Account.FK_ACCOUNT_ID)
    private Set<User> users = new HashSet(100);

//  --------------------------------------------------------------------------------------------------------------------

    public Account() {
        // default
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public Set<User> getUsers() {
        return users;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
