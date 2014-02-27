package org.notes.plugin.tf;

import org.apache.commons.lang.StringUtils;

public enum Output {
    SQL, RAW;

    public static Output fromString(String value) {
        for (Output o : values()) {
            if (StringUtils.equalsIgnoreCase(value, o.name())) {
                return o;
            }
        }
        return null;
    }
}
