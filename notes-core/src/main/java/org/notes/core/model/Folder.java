package org.notes.core.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity(name = "Folder")
@Table(name = "Folder",
        uniqueConstraints = @UniqueConstraint(columnNames = {User.FK_OWNER_ID, "parent_id", "name"})
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
public class Folder extends Node {

    public static final String QUERY_BY_ID = "Folder.QUERY_BY_ID";
    public static final String QUERY_GET_CHILDREN = "Folder.QUERY_GET_CHILDREN";
    public static final String QUERY_BY_VALUE = "Folder.QUERY_BY_VALUE";
    public static final String QUERY_ALL = "Folder.QUERY_ALL";
    public static final String QUERY_USERS_NOTEBOOKS = "Folder.QUERY_USERS_NOTEBOOKS";
    public static final String FK_FOLDER_ID = "folder_id";

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = Folder.FK_FOLDER_ID)
    private List<Document> notes = new LinkedList<Document>();

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = "parent_id")
    private Folder parent;

    @JsonIgnore
    @Column(updatable = false, insertable = false, nullable = true, name = "parent_id")
    private Long parentId;

    @Column(updatable = false, insertable = false, nullable = true, name = Database.FK_DATABASE_ID)
    private Long databaseId;

    public Folder() {
        //
    }

    public List<Document> getNotes() {
        return notes;
    }

    public void setNotes(List<Document> notes) {
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

    public Long getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Long databaseId) {
        this.databaseId = databaseId;
    }

}
