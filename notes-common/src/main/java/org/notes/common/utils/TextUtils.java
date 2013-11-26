package org.notes.common.utils;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.notes.common.configuration.Configuration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextUtils {

    private static final Pattern TRIM_PATTERN = Pattern.compile("^[\\W \n\t]+|[\\W \n\t]+$");

    public static String trim(String str) {

        if (StringUtils.isBlank(str)) {
            return null;

        } else {
            Matcher m = TRIM_PATTERN.matcher(str);
            StringBuffer sb = new StringBuffer(50);
            while (m.find()) {
                m.appendReplacement(sb, "");
            }
            m.appendTail(sb);
            return sb.toString();
        }
    }

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

        Cleaner cleaner = new Cleaner(Whitelist.simpleText());
        Document cleaned = cleaner.clean(Jsoup.parse(outline.toString()));

        return StringUtils.substring(cleaned.body().html(), 0, outlineSize);
    }

    private static String norm(String text) {
        return trim(text.replaceAll("[\n\t\r ]+", " "));
    }
}
