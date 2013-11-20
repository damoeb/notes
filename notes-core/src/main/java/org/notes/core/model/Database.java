package org.notes.core.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.common.ForeignKey;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "DDatabase")
@Table(name = "DDatabase",
        uniqueConstraints = @UniqueConstraint(columnNames = {ForeignKey.OWNER_ID, "name"})
)
@NamedQueries({
        @NamedQuery(name = Database.QUERY_BY_ID, query = "SELECT a FROM DDatabase a where a.id=:ID"),
        @NamedQuery(name = Database.QUERY_ALL, query = "SELECT a FROM DDatabase a where a.ownerId=:USER_ID")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Database extends Node {

    public static final String QUERY_BY_ID = "Database.QUERY_BY_ID";
    public static final String QUERY_ALL = "Database.QUERY_ALL";
    public static final String FK_DATABASE_ID = "database_id";

    @OneToMany(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = Database.FK_DATABASE_ID)
    private Set<Folder> folders = new HashSet(100);

    @Basic
    private Long selectedFolderId;

    public Database() {
        //
    }

    public Set<Folder> getFolders() {
        return folders;
    }

    public void setFolders(Set<Folder> folders) {
        this.folders = folders;
    }

    public Long getSelectedFolderId() {
        return selectedFolderId;
    }

    public void setSelectedFolderId(Long selectedFolderId) {
        this.selectedFolderId = selectedFolderId;
    }
}
