package org.notes.core.domain;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.enterprise.context.SessionScoped;
import java.util.Set;

@SessionScoped
//@StatefulTimeout(unit = TimeUnit.MINUTES, value = 30)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class StandardNotesSession implements NotesSession {

    private User user;

    private Set<StandardDatabase> databases;

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
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
