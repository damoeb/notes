package org.notes.common.bundle;

import org.apache.commons.lang.StringUtils;
import org.notes.common.exceptions.NotesException;

import java.util.*;

/**
 * @author Daniel Scheidle, daniel.scheidle@ucs.at
 *         11:40, 17.07.12
 */
public class ResourceBundle {

    private static final String BUNDLE_BASE_NAME = "bundle";

    public static java.util.ResourceBundle get(Locale locale) throws NotesException {
        return PropertyResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
    }

    public static String getString(Locale locale, String key) throws NotesException {
        return get(locale).getString(key);
    }

    public static Map<String, String> getEntries(Locale locale, String prefix) throws NotesException {
        Map<String, String> map = new HashMap<String, String>();

        java.util.ResourceBundle bundle = get(locale);
        Enumeration<String> keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            if (StringUtils.startsWithIgnoreCase(key, prefix)) {
                map.put(key, bundle.getString(key));
            }
        }

        return map;
    }
}
