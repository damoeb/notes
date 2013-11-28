package org.notes.common;

import org.hibernate.cfg.ImprovedNamingStrategy;

public class CustomNamingStrategy extends ImprovedNamingStrategy {

    @Override
    public String tableName(String tableName) {
        return "NOTES_" + super.tableName(tableName);
    }
}
