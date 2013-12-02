package org.notes.core.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.common.ForeignKey;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * A logical collection of documents. Two different <code>databases</code> are disjunct.
 */
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

//  -- References ------------------------------------------------------------------------------------------------------

    // todo add settings like open documents, open folders, selected folder...

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(name = Database.FK_DATABASE_ID)
    private Set<Folder> folders = new HashSet(100);

//  --------------------------------------------------------------------------------------------------------------------

    public Database() {
        // default
    }

    public Database(long id) {
        setId(id);
    }

    public Set<Folder> getFolders() {
        return folders;
    }

    public void setFolders(Set<Folder> folders) {
        this.folders = folders;
    }
}
