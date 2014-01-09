package org.notes.core.interfaces;

import org.notes.core.model.Database;
import org.notes.core.model.User;

import java.io.Serializable;
import java.util.Set;

public interface SessionData extends Serializable {

    User getUser();

    Set<Database> getDatabases();

    void setDatabases(Set<Database> databases);

    void setUser(User user);
}
