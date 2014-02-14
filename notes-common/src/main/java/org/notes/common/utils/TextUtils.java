package org.notes.common.utils;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.notes.common.configuration.Configuration;

public final class TextUtils {

    public static String toOutline(String first, String... more) {
        int outlineSize = Configuration.Constants.OUTLINE_LENGTH;
        StringBuilder outline = new StringBuilder(outlineSize * 2);

        outline.append(norm(first));
        outline.append(" ");

        for (String text : more) {
            if (StringUtils.isBlank(text)) {
                continue;
            }
            if (outline.length() > outlineSize) {
                break;
            }
            String normalized = norm(text);
            outline.append(normalized);
            outline.append(" ");
        }

        return StringUtils.substring(cleanHtml(outline.toString()), 0, outlineSize);
    }

    public static String cleanHtml(String html) {

        Cleaner cleaner = new Cleaner(Whitelist.simpleText());
        Document cleaned = cleaner.clean(Jsoup.parse(html));

        return cleaned.body().text();
    }

    private static String norm(String text) {
        return text.replaceAll("[\n\t\r ]+", " ");
    }
}
