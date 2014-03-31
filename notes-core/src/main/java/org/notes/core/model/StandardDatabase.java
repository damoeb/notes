package org.notes.core.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.common.model.Database;
import org.notes.common.model.Folder;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * A logical collection of documents. Two different <code>databases</code> are disjunct.
 */
@Entity(name = "DDatabase")
@Table(name = "DDatabase")
@NamedQueries({
        @NamedQuery(name = StandardDatabase.QUERY_BY_ID, query = "SELECT a FROM DDatabase a where a.id=:ID"),
        @NamedQuery(name = StandardDatabase.QUERY_BY_USER, query = "SELECT a FROM DDatabase a where a.owner=:USER")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class StandardDatabase extends Node implements Database {

    public static final String QUERY_BY_ID = "StandardDatabase.QUERY_BY_ID";
    public static final String QUERY_BY_USER = "StandardDatabase.QUERY_BY_USER";
    //
    public static final String FK_DATABASE_ID = "database_id";
    public static final String ACTIVE_FOLDER_ID = "active_folder_id";
    public static final String DEFAULT_FOLDER_ID = "default_folder_id";
    public static final String TRASH_FOLDER_ID = "trash_folder_id";

//  -- References ------------------------------------------------------------------------------------------------------

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = {}, targetEntity = StandardFolder.class)
    @JoinColumn(name = StandardDatabase.FK_DATABASE_ID)
    private Set<Folder> folders = new HashSet(10);

    @JsonIgnore
    @OneToOne(cascade = {}, fetch = FetchType.LAZY, optional = true, targetEntity = StandardFolder.class)
    @JoinColumn(name = ACTIVE_FOLDER_ID)
    private Folder activeFolder;

    @Column(insertable = false, updatable = false, name = ACTIVE_FOLDER_ID, nullable = true)
    private Long activeFolderId;

    @JsonIgnore
    @OneToOne(cascade = {}, fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = DEFAULT_FOLDER_ID)
    private StandardFolder defaultFolder;

    @Column(insertable = false, updatable = false, name = DEFAULT_FOLDER_ID, nullable = true)
    private Long defaultFolderId;

    @JsonIgnore
    @OneToOne(cascade = {}, fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = TRASH_FOLDER_ID)
    private StandardFolder trashFolder;

    @Column(insertable = false, updatable = false, name = TRASH_FOLDER_ID, nullable = true)
    private Long trashFolderId;

//  --------------------------------------------------------------------------------------------------------------------

    public StandardDatabase() {
        // default
    }

    public StandardDatabase(long id) {
        setId(id);
    }

    public StandardDatabase(long id, int documentCount, Date modified) {
        setId(id);
        setDocumentCount(documentCount);
        setModified(modified);
    }

    public Set<Folder> getFolders() {
        return folders;
    }

    public Folder getActiveFolder() {
        return activeFolder;
    }

    public void setActiveFolder(Folder activeFolder) {
        this.activeFolder = activeFolder;
    }

    public Long getActiveFolderId() {
        return activeFolderId;
    }

    public void setActiveFolderId(Long activeFolderId) {
        this.activeFolderId = activeFolderId;
    }

    public StandardFolder getDefaultFolder() {
        return defaultFolder;
    }

    public void setDefaultFolder(StandardFolder defaultFolder) {
        this.defaultFolder = defaultFolder;
    }

    public Long getDefaultFolderId() {
        return defaultFolderId;
    }

    public void setDefaultFolderId(Long defaultFolderId) {
        this.defaultFolderId = defaultFolderId;
    }

    public StandardFolder getTrashFolder() {
        return trashFolder;
    }

    public void setTrashFolder(StandardFolder trashFolder) {
        this.trashFolder = trashFolder;
    }

    public Long getTrashFolderId() {
        return trashFolderId;
    }

    public void setTrashFolderId(Long trashFolderId) {
        this.trashFolderId = trashFolderId;
    }
}
