package org.notes.core.domain;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.enterprise.context.SessionScoped;
import java.util.Set;

@SessionScoped
//@StatefulTimeout(unit = TimeUnit.MINUTES, value = 30)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class StandardNotesSession implements NotesSession {

    private String userId;
    private Long defaultFolderId;
    private Long trashFolderId;
    private Long activeFolderId;

    private Set<StandardDatabase> databases;

    @Override
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public Long getDefaultFolderId() {
        return defaultFolderId;
    }

    public void setDefaultFolderId(Long defaultFolderId) {
        this.defaultFolderId = defaultFolderId;
    }

    @Override
    public Long getTrashFolderId() {
        return trashFolderId;
    }

    public void setTrashFolderId(Long trashFolderId) {
        this.trashFolderId = trashFolderId;
    }

    @Override
    public Long getActiveFolderId() {
        return activeFolderId;
    }

    public void setActiveFolderId(Long activeFolderId) {
        this.activeFolderId = activeFolderId;
    }

    @Override
    public Set<StandardDatabase> getDatabases() {
        return databases;
    }

    @Override
    public void setDatabases(Set<StandardDatabase> databases) {
        this.databases = databases;
    }
}
