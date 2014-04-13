package org.notes.common.utils;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.notes.common.configuration.Configuration;

import java.io.IOException;

public final class TextUtils {

//    private static final Pattern ASCII_PATTERN = Pattern.compile("[^\\p{ASCII}]");

    public static String normedTerm(String term) {
//        term = Normalizer.normalize(term, Normalizer.Form.NFD);
//
//        return ASCII_PATTERN.matcher(term).replaceAll("").toLowerCase();
        return StringUtils.lowerCase(term);
    }

    public static String toOutline(String first, String... more) {
        if (first == null) {
            return "";
        }

        int outlineSize = Configuration.Constants.OUTLINE_LENGTH;
        StringBuilder outlineSb = new StringBuilder(outlineSize * 2);

        outlineSb.append(normWhitespaces(first));
        outlineSb.append(" ");

        for (String text : more) {
            if (StringUtils.isBlank(text)) {
                continue;
            }
            if (outlineSb.length() > outlineSize) {
                break;
            }
            String normalized = normWhitespaces(text);
            outlineSb.append(normalized);
            outlineSb.append(" ");
        }

        return StringUtils.substring(cleanHtml(outlineSb.toString()), 0, outlineSize);
    }

    public static String cleanHtml(String html) {

        Document document = Jsoup.parse(html);

        String text = document.body().text();

        // remove non-breaking spaces
        text = text.replace("\u00a0", " ");

        return text;
    }

    public static String cleanHtmlRelaxed(String html) {

        Cleaner cleaner = new Cleaner(Whitelist.relaxed());
        Document cleaned = cleaner.clean(Jsoup.parse(html));

        return cleaned.body().html();
    }

    private static String normWhitespaces(String text) {
        return text.replaceAll("[\n\t\r ]+", " ");
    }

    public static String toJson(Object obj) {
        try {
            ObjectWriter ow = new ObjectMapper().writer();
            return ow.writeValueAsString(obj);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Object fromJson(String jsonString, Class<?> clazz) {
        try {
            return new ObjectMapper().readValue(jsonString, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
