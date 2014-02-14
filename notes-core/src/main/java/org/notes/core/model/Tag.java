package org.notes.core.model;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "Tag")
@Table(name = "Tag")
@NamedQueries({
        @NamedQuery(name = Tag.QUERY_BY_ID, query = "SELECT a FROM Tag a where a.id=:ID"),
        @NamedQuery(name = Tag.QUERY_BY_NAME, query = "SELECT a FROM Tag a where a.name=:NAME"),
        @NamedQuery(name = Tag.QUERY_ALL, query = "SELECT a FROM Tag a")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Tag implements Serializable {

    public static final String QUERY_BY_ID = "Tag.QUERY_BY_ID";
    public static final String QUERY_ALL = "Tag.QUERY_ALL";
    public static final String QUERY_BY_NAME = "Tag.QUERY_BY_NAME";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Basic
    @Column(nullable = false, unique = true)
    private String name;

//  --------------------------------------------------------------------------------------------------------------------

    public Tag() {
        // default
    }

    public Tag(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tag) {
            Tag other = (Tag) obj;
            return StringUtils.equals(other.getName(), getName());
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return getName() != null ? getName().hashCode() : super.hashCode();
    }
}
