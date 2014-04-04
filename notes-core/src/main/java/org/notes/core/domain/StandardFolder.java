package org.notes.core.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.common.ForeignKey;
import org.notes.common.domain.Folder;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Belongs of a database, used to classify documents.
 */
@Entity(name = "Folder")
@Table(name = "StandardFolder",
        uniqueConstraints = @UniqueConstraint(columnNames = {ForeignKey.USER, "level", "name"})
)
@NamedQueries({
        @NamedQuery(name = StandardFolder.QUERY_BY_ID, query = "SELECT a FROM Folder a where a.id=:ID"),
        @NamedQuery(name = StandardFolder.QUERY_CHILDREN, query = "SELECT new Folder(a.id, a.name, a.leaf, a.documentCount, a.modified, a.level, a.expanded) FROM Folder a WHERE a.parentId = :ID"),
        @NamedQuery(name = StandardFolder.QUERY_ROOT_FOLDERS, query = "SELECT new Folder(a.id, a.name, a.leaf, a.documentCount, a.modified, a.level, a.expanded) FROM Folder a WHERE a.databaseId = :DB_ID and a.owner = :OWNER and a.level = 0 ORDER BY a.name")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class StandardFolder extends Node implements Folder {

    public static final String QUERY_BY_ID = "StandardFolder.QUERY_BY_ID";
    public static final String QUERY_CHILDREN = "StandardFolder.QUERY_CHILDREN";
    public static final String QUERY_ROOT_FOLDERS = "StandardFolder.QUERY_ROOT_FOLDERS";

    @Basic
    @Column(nullable = false)
    protected String name;

    @Basic
    private Integer level = 0;

    /**
     * true, if has no children folders
     */
    @Basic
    private Boolean leaf = true;

    /**
     * true, if not leaf and child nodes are shown
     */
    @Basic
    private Boolean expanded = false;

//  -- References ------------------------------------------------------------------------------------------------------

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = {}, targetEntity = StandardFolder.class)
    @JoinColumn(name = "parent_id")
    private Folder parent;

    @Column(updatable = false, insertable = false, nullable = true, name = "parent_id")
    private Long parentId;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = ForeignKey.FOLDER_ID)
    private Set<BasicDocument> documents = new HashSet(100);

    @Column(updatable = false, insertable = false, nullable = true, name = StandardDatabase.FK_DATABASE_ID)
    private Long databaseId;

//  --------------------------------------------------------------------------------------------------------------------

    public StandardFolder() {
        // default
    }

    public StandardFolder(long id, String name, boolean leaf, int documentCount, Date modified, int level, boolean expanded) {
        setId(id);
        setLeaf(leaf);
        setName(name);
        setDocumentCount(documentCount);
        setModified(modified);
        setLevel(level);
        setExpanded(expanded);
    }

    public StandardFolder(long id) {
        setId(id);
        expanded = null;
        leaf = null;
        level = null;
        documentCount = null;
    }

    public Set<BasicDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<BasicDocument> documents) {
        this.documents = documents;
    }

    @Override
    public Folder getParent() {
        return parent;
    }

    public void setParent(Folder parent) {
        this.parent = parent;
    }

    public Long getParentId() {
        return parentId;
    }

    protected void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getDatabaseId() {
        return databaseId;
    }

    protected void setDatabaseId(Long databaseId) {
        this.databaseId = databaseId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(Boolean leaf) {
        this.leaf = leaf;
    }

    public Boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(Boolean expanded) {
        this.expanded = expanded;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
