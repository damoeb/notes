package org.notes.core.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.common.ForeignKey;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * A logical collection of documents. Two different <code>databases</code> are disjunct.
 */
@Entity(name = "DDatabase")
@Table(name = "DDatabase",
        uniqueConstraints = @UniqueConstraint(columnNames = {ForeignKey.OWNER, "name"})
)
@NamedQueries({
        @NamedQuery(name = Database.QUERY_BY_ID, query = "SELECT a FROM DDatabase a where a.id=:ID"),
        @NamedQuery(name = Database.QUERY_OPEN_FOLDERS, query = "SELECT new Folder(b.id) FROM DDatabase a INNER JOIN a.openFolders b where a.id=:ID"),
        @NamedQuery(name = Database.QUERY_ALL, query = "SELECT new DDatabase(a.id, a.name, a.documentCount, a.modified) FROM DDatabase a where a.owner=:USER")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Database extends Node {

    public static final String QUERY_BY_ID = "Database.QUERY_BY_ID";
    public static final String QUERY_ALL = "Database.QUERY_ALL";
    public static final String QUERY_OPEN_FOLDERS = "Database.QUERY_OPEN_FOLDERS";
    //
    public static final String FK_DATABASE_ID = "database_id";

//  -- References ------------------------------------------------------------------------------------------------------

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = Database.FK_DATABASE_ID)
    private Set<Folder> folders = new HashSet(10);

    @OneToMany(fetch = FetchType.LAZY, cascade = {})
    @JoinTable(name = "database2open_folder")
    private Set<Folder> openFolders = new HashSet(10);

    // todo check active folder

    @JsonIgnore
    @OneToOne(cascade = {}, fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = ForeignKey.FOLDER_ID)
    private Folder activeFolder;

    @Column(insertable = false, updatable = false, name = ForeignKey.FOLDER_ID, nullable = true)
    private Long activeFolderId;

//  --------------------------------------------------------------------------------------------------------------------

    public Database() {
        // default
    }

    public Database(long id) {
        setId(id);
    }

    public Database(long id, String name, int documentCount, Date modified) {
        setId(id);
        setName(name);
        setDocumentCount(documentCount);
        setModified(modified);
        openFolders = null;
    }

    public Set<Folder> getFolders() {
        return folders;
    }

    public Set<Folder> getOpenFolders() {
        return openFolders;
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

    public void setOpenFolders(Set<Folder> openFolders) {
        this.openFolders = openFolders;
    }
}
