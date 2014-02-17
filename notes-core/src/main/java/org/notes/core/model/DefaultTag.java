package org.notes.core.model;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.common.model.Tag;

import javax.persistence.*;

@Entity(name = "Tag")
@Table(name = "Tag")
@NamedQueries({
        @NamedQuery(name = DefaultTag.QUERY_BY_ID, query = "SELECT a FROM Tag a where a.id=:ID"),
        @NamedQuery(name = DefaultTag.QUERY_BY_NAME, query = "SELECT a FROM Tag a where a.name=:NAME"),
        @NamedQuery(name = DefaultTag.QUERY_USER_NETWORK, query = "SELECT new Tag(t.name, COUNT(t.id)) FROM BasicDocument d INNER JOIN d.tags t where d.owner=:USER GROUP BY t.name ORDER BY COUNT(t.name) DESC"),
        @NamedQuery(name = DefaultTag.QUERY_ALL, query = "SELECT a FROM Tag a")
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DefaultTag implements Tag {

    public static final String QUERY_BY_ID = "DefaultTag.QUERY_BY_ID";
    public static final String QUERY_ALL = "DefaultTag.QUERY_ALL";
    public static final String QUERY_BY_NAME = "DefaultTag.QUERY_BY_NAME";
    public static final String QUERY_USER_NETWORK = "DefaultTag.QUERY_USER_NETWORK";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Basic
    @Column(nullable = false)
    private String name;

    @Transient
    private Long frequency;

//  --------------------------------------------------------------------------------------------------------------------

    public DefaultTag() {
        // default
    }

    public DefaultTag(String name) {
        this.name = name;
    }

    public DefaultTag(String name, Long frequency) {
        this(name);
        this.frequency = frequency;
    }

    public Long getFrequency() {
        return frequency;
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
        if (obj instanceof DefaultTag) {
            DefaultTag other = (DefaultTag) obj;
            return StringUtils.equals(other.getName(), getName());
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return getName() != null ? getName().hashCode() : super.hashCode();
    }
}
