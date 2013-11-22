package org.notes.common.model;

public enum Trigger {
    INDEX,
    // INDEXED, todo should update outline of referenced documents?
    EXTRACT,
    OUTLINE,
    EXTRACT_FAILED,
    DELETE
}
