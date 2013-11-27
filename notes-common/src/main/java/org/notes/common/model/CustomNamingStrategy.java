package org.notes.common.model;

import org.hibernate.cfg.ImprovedNamingStrategy;

public class CustomNamingStrategy extends ImprovedNamingStrategy {

    @Override
    public String tableName(String tableName) {
        return "NOTES_" + super.tableName(tableName);
    }
}
