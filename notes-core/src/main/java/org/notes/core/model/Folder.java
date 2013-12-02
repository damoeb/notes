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
        uniqueConstraints = @UniqueConstraint(columnNames = {ForeignKey.OWNER_ID, "parent_id", "name"})
)
@NamedQueries({
        @NamedQuery(name = Folder.QUERY_BY_ID, query = "SELECT a FROM Folder a where a.id=:ID"),
        @NamedQuery(name = Folder.QUERY_DOCUMENTS, query = "SELECT new BasicDocument(a.id, a.title, a.outline, a.kind, a.progress, a.reminder, a.modified) FROM BasicDocument a where a.folderId=:ID AND a.deleted=false"),
        @NamedQuery(name = Folder.QUERY_RELATED_DOCUMENTS, query = "SELECT new BasicDocument(a.id, a.title, a.outline, a.kind, a.progress, a.reminder, a.modified) FROM Folder f JOIN f.inheritedDocuments a WHERE f.id=:ID AND a.deleted=false"),
        @NamedQuery(name = Folder.QUERY_CHILDREN, query = "SELECT new Folder(a.id, a.name, a.leaf, a.documentCount, a.modified, a.level) FROM Folder a WHERE a.parentId = :ID"),
        @NamedQuery(name = Folder.QUERY_ROOT_FOLDERS, query = "SELECT new Folder(a.id, a.name, a.leaf, a.documentCount, a.modified, a.level) FROM Folder a WHERE a.databaseId = :DB_ID and a.ownerId = :OWNER_ID and a.level = 0")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Folder extends Node {

    public static final String QUERY_BY_ID = "Folder.QUERY_BY_ID";
    public static final String QUERY_CHILDREN = "Folder.QUERY_CHILDREN";
    public static final String QUERY_ROOT_FOLDERS = "Folder.QUERY_ROOT_FOLDERS";
    public static final String QUERY_DOCUMENTS = "Folder.QUERY_DOCUMENTS";
    public static final String QUERY_RELATED_DOCUMENTS = "Folder.QUERY_RELATED_DOCUMENTS";

    @Basic
    private Integer level = 0;

    /**
     * true, if has no children folders
     */
    @Basic
    private boolean leaf = true;

    /**
     * true, if not leaf and child nodes are shown
     */
    // todo remove, put in settings
    @Basic
    private boolean expanded = false;

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
    @JoinTable(name = "folder2document", joinColumns = @JoinColumn(name = ForeignKey.FOLDER_ID), inverseJoinColumns = @JoinColumn(name = BasicDocument.FK_DOCUMENT_ID))
    private Set<BasicDocument> inheritedDocuments = new HashSet(100);

    @Column(updatable = false, insertable = false, nullable = true, name = Database.FK_DATABASE_ID)
    private Long databaseId;

//  -- Transient -------------------------------------------------------------------------------------------------------

    @Transient
    private Set<Folder> children = null;

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

    public Set<BasicDocument> getInheritedDocuments() {
        return inheritedDocuments;
    }

    public void setInheritedDocuments(Set<BasicDocument> inheritedDocuments) {
        this.inheritedDocuments = inheritedDocuments;
    }
}
