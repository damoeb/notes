package org.notes.core.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity(name = "Folder")
@Table(name = "Folder",
        uniqueConstraints = @UniqueConstraint(columnNames = {"parent_id", "name"})
)
@NamedQueries({
        @NamedQuery(name = Folder.QUERY_BY_ID, query = "SELECT a FROM Folder a where a.id=:ID"),
        @NamedQuery(name = Folder.QUERY_GET_CHILDREN, query = "SELECT a FROM Folder a where a.parentId=:PARENT_ID"),
        @NamedQuery(name = Folder.QUERY_BY_VALUE, query = "SELECT a FROM Folder a where LOWER(a.name)=LOWER(:VAL)"),
        @NamedQuery(name = Folder.QUERY_ALL, query = "SELECT a FROM Folder a"),
        @NamedQuery(name = Folder.QUERY_USERS_NOTEBOOKS, query = "SELECT a FROM Folder a where a.ownerId=:ID and a.parentId IS NULL")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Folder implements Serializable {

    public static final String QUERY_BY_ID = "Folder.QUERY_BY_ID";
    public static final String QUERY_GET_CHILDREN = "Folder.QUERY_GET_CHILDREN";
    public static final String QUERY_BY_VALUE = "Folder.QUERY_BY_VALUE";
    public static final String QUERY_ALL = "Folder.QUERY_ALL";
    public static final String QUERY_USERS_NOTEBOOKS = "Folder.QUERY_USERS_NOTEBOOKS";
    public static final String FK_FOLDER_ID = "folder_id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(updatable = false, insertable = false, name = User.FK_OWNER_ID)
    private Long ownerId;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = Folder.FK_FOLDER_ID)
    private List<Note> notes = new LinkedList<Note>();

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = "parent_id")
    private Folder parent;

    @JsonIgnore
    @Column(updatable = false, insertable = false, nullable = true, name = "parent_id")
    private Long parentId;

    @Basic
    @Index(name = "nameIdx")
    @Column(nullable = false)
    private String name;

    @Basic
    private boolean deleted;


    public Folder() {
        //
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public Folder getParent() {
        return parent;
    }

    public void setParent(Folder parent) {
        this.parent = parent;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
