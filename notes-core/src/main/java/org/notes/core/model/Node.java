package org.notes.core.model;

import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
public abstract class Node implements Serializable {

    @Column(updatable = false, insertable = false, name = User.FK_OWNER_ID)
    private Long ownerId;

    @Basic
    private Long documentCount;

    @Basic
    @Column(nullable = false)
    private String name;

    @Basic
    private boolean deleted;


    public Node() {
        //
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getDocumentCount() {
        return documentCount;
    }

    public void setDocumentCount(Long documentCount) {
        this.documentCount = documentCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
