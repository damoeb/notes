package org.notes.core.model;

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
        @NamedQuery(name = Account.QUERY_BY_ID, query = "SELECT a FROM Account a where a.id=:ID"),
        @NamedQuery(name = Account.QUERY_ALL, query = "SELECT a FROM Account a")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Account implements Serializable {

    public static final String QUERY_BY_ID = "Account.QUERY_BY_ID";
    public static final String QUERY_ALL = "Account.QUERY_ALL";
    public static final String FK_ACCOUNT_ID = "account_id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Basic
    @Column(nullable = false, unique = true)
    private String name;

    @Basic
    private long quota;

//  -- References ------------------------------------------------------------------------------------------------------

    @JsonIgnore
    @OneToMany(cascade = {}, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = Account.FK_ACCOUNT_ID)
    private Set<User> users = new HashSet(100);

//  --------------------------------------------------------------------------------------------------------------------

    public Account() {
        // default
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getQuota() {
        return quota;
    }

    public void setQuota(long quota) {
        this.quota = quota;
    }

    public Set<User> getUsers() {
        return users;
    }
}
