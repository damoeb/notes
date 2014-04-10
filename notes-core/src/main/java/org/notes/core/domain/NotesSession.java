package org.notes.core.domain;

import java.io.Serializable;
import java.util.Set;

public interface NotesSession extends Serializable {

    User getUser();

    Set<StandardDatabase> getDatabases();

    void setDatabases(Set<StandardDatabase> databases);

    void setUser(User user);
}
