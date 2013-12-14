package org.notes.core.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.common.ForeignKey;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Belongs of a database, used to classify documents.
 */
@Entity(name = "Folder")
@Table(name = "Folder",
        uniqueConstraints = @UniqueConstraint(columnNames = {ForeignKey.OWNER_ID, "level", "name"})
)
@NamedQueries({
        @NamedQuery(name = Folder.QUERY_BY_ID, query = "SELECT a FROM Folder a where a.id=:ID"),
        @NamedQuery(name = Folder.QUERY_CHILDREN, query = "SELECT new Folder(a.id, a.name, a.leaf, a.documentCount, a.modified, a.level) FROM Folder a WHERE a.parentId = :ID"),
        @NamedQuery(name = Folder.QUERY_ROOT_FOLDERS, query = "SELECT new Folder(a.id, a.name, a.leaf, a.documentCount, a.modified, a.level) FROM Folder a WHERE a.databaseId = :DB_ID and a.ownerId = :OWNER_ID and a.level = 0"),
        @NamedQuery(name = Folder.QUERY_OPEN_FOLDERS, query = "SELECT new Folder(o.id) FROM DDatabase a INNER JOIN a.openFolders o WHERE a.id = :DB_ID and a.ownerId = :OWNER_ID")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Folder extends Node {

    public static final String QUERY_BY_ID = "Folder.QUERY_BY_ID";
    public static final String QUERY_CHILDREN = "Folder.QUERY_CHILDREN";
    public static final String QUERY_ROOT_FOLDERS = "Folder.QUERY_ROOT_FOLDERS";
    public static final String QUERY_OPEN_FOLDERS = "Folder.QUERY_OPEN_FOLDERS";

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
    // todo remove, put in settings
    @Basic
    private Boolean expanded = false;

//  -- References ------------------------------------------------------------------------------------------------------

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = "parent_id")
    private Folder parent;

    @Column(updatable = false, insertable = false, nullable = true, name = "parent_id")
    private Long parentId;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = ForeignKey.FOLDER_ID)
    private Set<BasicDocument> documents = new HashSet(100);

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = {})
    @JoinTable(name = "folder2related_doc", joinColumns = @JoinColumn(name = ForeignKey.FOLDER_ID), inverseJoinColumns = @JoinColumn(name = BasicDocument.FK_DOCUMENT_ID))
    private Set<BasicDocument> inheritedDocuments = new HashSet(100);

    @Column(updatable = false, insertable = false, nullable = true, name = Database.FK_DATABASE_ID)
    private Long databaseId;

//  --------------------------------------------------------------------------------------------------------------------

    public Folder() {
        // default
    }

    public Folder(long id, String name, boolean leaf, int documentCount, Date modified, int level) {
        setId(id);
        setLeaf(leaf);
        setName(name);
        setDocumentCount(documentCount);
        setModified(modified);
        setLevel(level);
    }

    public Folder(long id) {
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

    public Set<BasicDocument> getInheritedDocuments() {
        return inheritedDocuments;
    }

    public void setInheritedDocuments(Set<BasicDocument> inheritedDocuments) {
        this.inheritedDocuments = inheritedDocuments;
    }
}
