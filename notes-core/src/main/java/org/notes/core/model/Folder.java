package org.notes.core.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "Folder")
@Table(name = "Folder",
        uniqueConstraints = @UniqueConstraint(columnNames = {User.FK_OWNER_ID, "parent_id", "name"})
)
@NamedQueries({
        @NamedQuery(name = Folder.QUERY_BY_ID, query = "SELECT a FROM Folder a where a.id=:ID"),
        @NamedQuery(name = Folder.QUERY_DOCUMENTS, query = "SELECT new Document(a.id, a.title, a.outline, a.kind, a.created, a.modified) FROM Document a where a.folderId=:ID AND a.deleted=false"),
        @NamedQuery(name = Folder.QUERY_BY_VALUE, query = "SELECT a FROM Folder a where LOWER(a.name)=LOWER(:VAL)"),
        @NamedQuery(name = Folder.QUERY_ALL, query = "SELECT a FROM Folder a"),
        @NamedQuery(name = Folder.QUERY_USERS_NOTEBOOKS, query = "SELECT a FROM Folder a where a.ownerId=:ID and a.parentId IS NULL")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Folder extends Node {

    public static final String QUERY_BY_ID = "Folder.QUERY_BY_ID";
    public static final String QUERY_BY_VALUE = "Folder.QUERY_BY_VALUE";
    public static final String QUERY_ALL = "Folder.QUERY_ALL";
    public static final String QUERY_USERS_NOTEBOOKS = "Folder.QUERY_USERS_NOTEBOOKS";
    public static final String FK_FOLDER_ID = "folder_id";
    public static final String QUERY_DOCUMENTS = "Folder.QUERY_DOCUMENTS";

    @Basic
    private Integer level = 0;

    @OneToMany(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = Folder.FK_FOLDER_ID)
    private Set<Document> documents = new HashSet(100);

    @Transient
    private Set<Folder> children = null;

    /**
     * true, if has no children folders
     */
    @Basic
    private boolean leaf = true;

    /**
     * true, if not leaf and child nodes are shown
     */
    @Basic
    private boolean expanded = false;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = "parent_id")
    private Folder parent;

    @Column(updatable = false, insertable = false, nullable = true, name = "parent_id")
    private Long parentId;

    @Column(updatable = false, insertable = false, nullable = true, name = Database.FK_DATABASE_ID)
    private Long databaseId;

    public Folder() {
        //
    }

    public Set<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Set<Folder> getChildren() {
        return children;
    }

    public void setChildren(Set<Folder> children) {
        this.children = children;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
