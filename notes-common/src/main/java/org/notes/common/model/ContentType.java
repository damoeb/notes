package org.notes.common.model;

import org.apache.commons.lang.StringUtils;

public enum ContentType {
    PDF("application/pdf"), UNKNOWN(""), TEMP("");

    private final String identifier;

    ContentType(String identifier) {
        this.identifier = identifier;
    }

    public static ContentType fromString(String needle) {
        for (ContentType t : values()) {
            if (StringUtils.equalsIgnoreCase(needle, t.identifier)) {
                return t;
            }
        }
        return UNKNOWN;
    }

}
