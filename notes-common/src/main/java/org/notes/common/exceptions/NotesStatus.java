package org.notes.common.exceptions;

import org.apache.commons.lang.StringUtils;

/**
 * @author Daniel Scheidle, daniel.scheidle@ucs.at
 *         12:07, 12.07.12
 */
public enum NotesStatus {
    OK(0),
    CONFIGURATION_ERROR(100),
    PERSISTENCE_ERROR(200),
    SERVICE_ERROR(300),
    REQUEST_ERROR(400),
    ERROR(1000),
    CACHE_ERROR(2000),
    GEO_ERROR(3000),
    CRYPT_ERROR(3001),
    PARAMETER_TOO_LONG(4000),
    PARAMETER_MISSING(4001);

    private int statusCode;

    private NotesStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public NotesStatus fromStatusCode(int statusCode) {
        for (NotesStatus status : NotesStatus.values()) {
            if (status.getStatusCode() == statusCode) {
                return status;
            }
        }
        return null;
    }

    public NotesStatus fromString(String str) {
        for (NotesStatus status : NotesStatus.values()) {
            if (StringUtils.equalsIgnoreCase(status.toString(), str)) {
                return status;
            }
        }
        return null;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}
