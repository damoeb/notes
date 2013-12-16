package org.notes.core.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "Tag")
@Table(name = "Tag")
@NamedQueries({
        @NamedQuery(name = Tag.QUERY_BY_ID, query = "SELECT a FROM Tag a where a.id=:ID"),
        @NamedQuery(name = Tag.QUERY_ALL, query = "SELECT a FROM Tag a")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Tag implements Serializable {

    public static final String QUERY_BY_ID = "Tag.QUERY_BY_ID";
    public static final String QUERY_ALL = "Tag.QUERY_ALL";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Basic
    @Column(nullable = false)
    private String name;


//  -- References ------------------------------------------------------------------------------------------------------

//    @Column(insertable = false, updatable = false, name = ForeignKey.TAG_ID)
//    private Long tagId;

//    @JsonIgnore
//    @OneToMany(cascade = {}, fetch = FetchType.LAZY, orphanRemoval = true)
//    @JoinColumn(name = ForeignKey.OWNER_ID)
//    private Set<User> users = new HashSet(100);
//
//    @Column(name = Account.FK_ACCOUNT_ID, insertable = false, updatable = false)
//    private Long accountId;

//  --------------------------------------------------------------------------------------------------------------------

    public Tag() {
        // default
    }

    public Tag(String name) {
        this.name = name;
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
}
