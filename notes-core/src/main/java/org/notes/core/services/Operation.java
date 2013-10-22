package org.notes.core.services;

import org.apache.commons.lang.StringUtils;

public enum Operation {
    get_children, create_node, remove_node, rename_node;

    public static Operation byString(String value) {
        for(Operation op:values()) {
            if(StringUtils.equalsIgnoreCase(op.name(), value)) {
                return op;
            }
        }
        return null;
    }
}
