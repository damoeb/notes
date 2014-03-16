package org.notes.core.dao;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.notes.core.interfaces.SessionData;
import org.notes.core.model.StandardDatabase;
import org.notes.core.model.User;

import javax.enterprise.context.SessionScoped;
import java.util.Set;

@SessionScoped
//@StatefulTimeout(unit = TimeUnit.MINUTES, value = 30)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SessionDataBean implements SessionData {

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
