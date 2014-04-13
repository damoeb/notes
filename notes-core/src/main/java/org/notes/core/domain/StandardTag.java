package org.notes.core.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.common.domain.Tag;

import javax.persistence.*;

@Entity(name = "Tag")
@Table(name = "Tag")
@NamedQueries({
        @NamedQuery(name = StandardTag.QUERY_BY_ID, query = "SELECT a FROM Tag a where a.id=:ID"),
        @NamedQuery(name = StandardTag.QUERY_BY_NAME, query = "SELECT a FROM Tag a where a.name=:NAME"),
        @NamedQuery(name = StandardTag.QUERY_USER_NETWORK, query = "SELECT new Tag(t.name) FROM BasicDocument d INNER JOIN d.tags t where d.userId=:USERNAME GROUP BY t.name ORDER BY COUNT(t.name) DESC"),
        @NamedQuery(name = StandardTag.QUERY_ALL, query = "SELECT a FROM Tag a")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class StandardTag implements Tag {

    public static final String QUERY_BY_ID = "StandardTag.QUERY_BY_ID";
    public static final String QUERY_ALL = "StandardTag.QUERY_ALL";
    public static final String QUERY_BY_NAME = "StandardTag.QUERY_BY_NAME";
    public static final String QUERY_USER_NETWORK = "StandardTag.QUERY_USER_NETWORK";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Basic
    @Column(nullable = false)
    private String name;

//  --------------------------------------------------------------------------------------------------------------------

    public StandardTag() {
        // default
    }

    public StandardTag(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StandardTag) {
            StandardTag other = (StandardTag) obj;
            return StringUtils.equals(other.getName(), getName());
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return getName() != null ? getName().hashCode() : super.hashCode();
    }
}
