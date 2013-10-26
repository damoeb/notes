package org.notes.core.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity(name = "DDatabase")
@Table(name = "DDatabase",
        uniqueConstraints = @UniqueConstraint(columnNames = {User.FK_OWNER_ID, "name"})
)
@NamedQueries({
        @NamedQuery(name = Database.QUERY_BY_ID, query = "SELECT a FROM DDatabase a where a.id=:ID"),
        @NamedQuery(name = Database.QUERY_GET_CHILDREN, query = "SELECT a FROM DDatabase a where a.id=:ID"),
        @NamedQuery(name = Database.QUERY_ALL, query = "SELECT a FROM DDatabase a where a.ownerId=:USER_ID")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Database extends Node {

    public static final String QUERY_BY_ID = "Database.QUERY_BY_ID";
    public static final String QUERY_GET_CHILDREN = "Database.QUERY_GET_CHILDREN";
    public static final String QUERY_ALL = "Database.QUERY_ALL";
    public static final String FK_DATABASE_ID = "database_id";

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = Database.FK_DATABASE_ID)
    private List<Folder> folders = new LinkedList<Folder>();

    @Basic
    private Long activeFolderId;


    public Database() {
        //
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
