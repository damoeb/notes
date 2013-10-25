package org.notes.core.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

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
    public static final String FK_OWNER_ID = "owner_id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Basic
    @Index(name = "usernameIdx")
    @Column(nullable = false, unique = true)
    private String username;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = User.FK_OWNER_ID)
    private List<Document> notes = new LinkedList<Document>();

    @Column(name = Account.FK_ACCOUNT_ID, insertable = false, updatable = false)
    private Long accountId;

    @ManyToOne
    @JoinColumn(name = Account.FK_ACCOUNT_ID)
    private Account account;

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

    public List<Document> getNotes() {
        return notes;
    }

    public void setNotes(List<Document> notes) {
        this.notes = notes;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
