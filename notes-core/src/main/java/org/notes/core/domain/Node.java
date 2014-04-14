package org.notes.core.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.common.ForeignKey;
import org.notes.common.endpoints.CustomDateDeserializer;
import org.notes.common.endpoints.CustomDateSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Common base of StandardFolder and StandardDatabase
 */
@MappedSuperclass
public abstract class Node implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * the owner
     */
    @Column(updatable = false, insertable = false, name = ForeignKey.USER_ID)
    protected String userId;

    @Transient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ForeignKey.USER_ID)
    protected User user;

    @Basic
    protected Integer documentCount = 0;

    @JsonIgnore
    @Basic
    protected boolean deleted;

//  -- References ------------------------------------------------------------------------------------------------------

    @Column(nullable = false)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonSerialize(using = CustomDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

//  --------------------------------------------------------------------------------------------------------------------

    public Node() {
        // default
    }

    public String getUserId() {
        return userId;
    }

    public Integer getDocumentCount() {
        return documentCount;
    }

    public void setDocumentCount(Integer documentCount) {
        this.documentCount = documentCount;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
