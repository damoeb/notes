package org.notes.core.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.common.ForeignKey;
import org.notes.common.service.CustomDateDeserializer;
import org.notes.common.service.CustomDateSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Common base of Folder and Database
 */
@MappedSuperclass
public abstract class Node implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(updatable = false, insertable = false, name = ForeignKey.USER)
    protected String owner;

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

    public String getOwner() {
        return owner;
    }

    protected void setOwner(String owner) {
        this.owner = owner;
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

    protected void setId(long id) {
        this.id = id;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }
}
