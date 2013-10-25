package org.notes.core.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Entity(name = "Database")
@Table(name = "Database",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ownerId", "name"})
)
@NamedQueries({
        @NamedQuery(name = Database.QUERY_BY_ID, query = "SELECT a FROM Database a where a.id=:ID"),
        @NamedQuery(name = Database.QUERY_GET_CHILDREN, query = "SELECT a FROM Database a where a.parentId=:PARENT_ID"),
        @NamedQuery(name = Database.QUERY_BY_VALUE, query = "SELECT a FROM Database a where LOWER(a.name)=LOWER(:VAL)"),
        @NamedQuery(name = Database.QUERY_ALL, query = "SELECT a FROM Database a"),
        @NamedQuery(name = Database.QUERY_USERS_NOTEBOOKS, query = "SELECT a FROM Database a where a.ownerId=:ID and a.parentId IS NULL")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Database extends Node {

    public static final String QUERY_BY_ID = "Database.QUERY_BY_ID";
    public static final String QUERY_GET_CHILDREN = "Database.QUERY_GET_CHILDREN";
    public static final String QUERY_BY_VALUE = "Database.QUERY_BY_VALUE";
    public static final String QUERY_ALL = "Database.QUERY_ALL";
    public static final String QUERY_USERS_NOTEBOOKS = "Database.QUERY_USERS_NOTEBOOKS";
    public static final String FK_DATABASE_ID = "database_id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = Database.FK_DATABASE_ID)
    private List<Folder> folders = new LinkedList<Folder>();

    @Basic
    private Long activeFolderId;

    public Database() {
        //
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Folder> getFolders() {
        return folders;
    }

    public void setFolders(List<Folder> folders) {
        this.folders = folders;
    }

    public Long getActiveFolderId() {
        return activeFolderId;
    }

    public void setActiveFolderId(Long activeFolderId) {
        this.activeFolderId = activeFolderId;
    }
}
