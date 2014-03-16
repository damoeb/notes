package org.notes.core.interfaces;

import org.notes.core.model.StandardDatabase;
import org.notes.core.model.User;

import java.io.Serializable;
import java.util.Set;

public interface SessionData extends Serializable {

    User getUser();

    Set<StandardDatabase> getDatabases();

    void setDatabases(Set<StandardDatabase> databases);

    void setUser(User user);
}
