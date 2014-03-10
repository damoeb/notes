package org.notes.plugin.tf;

import org.apache.maven.plugin.logging.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.notes.common.model.TermFrequency;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

public class WikiDumpParser {

    private static final int MAX_ELEMENTS = 0;

    private static final Pattern URL_PATTERN = Pattern.compile("http[s]?://[^ ]+");
    private static final Pattern ASCII_PATTERN = Pattern.compile("[^\\p{ASCII}]");

    // ------------

    private final Log log;
    private final Integer minTermLength;
    private final Integer maxTermLength;
    private final String pathToWikiDumpXml;
    private final Integer logOnDocCount;
    private final Integer stopAfterDocCount;

    // ------------

    private final Map<String, TermFrequency> termFreqMap = new HashMap<>(MAX_ELEMENTS);

    private final SortedSet<TermFrequency> sortedTerms = new TreeSet<>(new Comparator<TermFrequency>() {

        @Override
        public int compare(TermFrequency t0, TermFrequency t1) {
            if (t0.getFrequency().compareTo(t1.getFrequency()) != 0) {
                return t0.getFrequency().compareTo(t1.getFrequency());
            }
            return t0.getTerm().compareTo(t1.getTerm());
        }

    });

    private final Map<String, String> termsInDocument = new HashMap<>(1000);

    // ------------

    private long documentCount = 0;

    public WikiDumpParser(Integer minTermLength, Integer maxTermLength, String pathToWikiDumpXml, Integer logOnDocCount, Integer stopAfterDocCount, Log log) {
        this.minTermLength = minTermLength;
        this.maxTermLength = maxTermLength;
        this.pathToWikiDumpXml = pathToWikiDumpXml;
        this.logOnDocCount = logOnDocCount;
        this.stopAfterDocCount = stopAfterDocCount;
        this.log = log;
    }

    public Log getLog() {
        return log;
    }

    public ParserResult parse() {

        getLog().info("------------------------------------------------------------------------");
        getLog().info("Processing dump");
        getLog().info("------------------------------------------------------------------------");
        getLog().info("");

        InputStream in = null;
        XMLStreamReader parser = null;

        long start = System.currentTimeMillis();

        try {

            in = new FileInputStream(pathToWikiDumpXml);
            XMLInputFactory factory = XMLInputFactory.newInstance();
            parser = factory.createXMLStreamReader(in);

            boolean title, text;
            title = text = false;

            while (parser.hasNext()) {

                switch (parser.getEventType()) {

                    case XMLStreamConstants.START_ELEMENT:

                        if (parser.getLocalName().equalsIgnoreCase("title")) {
                            title = true;
                        }

                        if (parser.getLocalName().equalsIgnoreCase("text")) {
                            text = true;
                        }

                        break;

                    case XMLStreamConstants.CHARACTERS:
                        if (!parser.isWhiteSpace() && (text)) {
                            String document = cleanHtml(parser.getText());
                            document = cleanUrls(document);
                            parseDocument(document);
                        }
                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        title = text = false;

                        break;
                }
                parser.next();
            }

            getLog().info("Finished dump in " + (System.currentTimeMillis() - start) / 1000d + "s");

            return new ParserResult(documentCount, sortedTerms);

        } catch (InterruptedException e) {

            getLog().info("Aborted parsing after " + (System.currentTimeMillis() - start) / 1000d + "s");

            return new ParserResult(documentCount, sortedTerms);

        } catch (Exception e) {
            getLog().error(e);
            throw new IllegalStateException(e);

        } finally {

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (parser != null) {
                try {
                    parser.close();
                } catch (XMLStreamException e) {
                }
            }
        }
    }

    private String cleanUrls(String plaintext) {
        return URL_PATTERN.matcher(plaintext).replaceAll("");
    }

    private Cleaner cleaner = new Cleaner(Whitelist.simpleText());

    private String cleanHtml(String html) {

        if (html.contains("<")) {
            Document cleaned = cleaner.clean(Jsoup.parse(html));

            return cleaned.body().text();
        } else {
            return html;
        }
    }

    private void parseDocument(final String text) throws InterruptedException {

        if (documentCount % logOnDocCount == 0) {
            getLog().info(documentCount + " documents parsed");
        }

        if (stopAfterDocCount != null && documentCount >= stopAfterDocCount) {
            getLog().info(String.format("Reached manual limit of %s documents - abort", stopAfterDocCount));
            throw new InterruptedException();
        }

        documentCount++;

        final StringTokenizer tokenizer = new StringTokenizer(text, " &\\_\"<>|!?=+–­~-*/„“()’`´_#'°^@€%$§[]{}\n\t :,;.ˈ¹−…₂»«%¬”‘·∴ʿ‰″");

        termsInDocument.clear();

        while (tokenizer.hasMoreTokens()) {

            final String token = tokenizer.nextToken();

            if (token.length() < minTermLength) {
                getLog().debug("'" + token + "' too short");
                continue;
            }

            if (token.length() > maxTermLength) {
                getLog().debug("'" + token + "' too long");
                continue;
            }

            termsInDocument.put(normTerm(token), token);
        }

        for (String normed : termsInDocument.keySet()) {
            pushTerm(normed, termsInDocument.get(normed));
        }
    }

    private void pushTerm(final String normed, String term) throws InterruptedException {

        if (termFreqMap.containsKey(normed)) {

            TermFrequency tf = termFreqMap.get(normed);

            sortedTerms.remove(tf);

            tf.setFrequency(tf.getFrequency() + 1);

            sortedTerms.add(tf);

        } else {

            TermFrequency tf = new TermFrequency();
            tf.setTerm(normed);
            tf.setOriginal(term);
            tf.setFrequency(1);

            termFreqMap.put(normed, tf);
            sortedTerms.add(tf);
        }
    }

    private static String normTerm(String term) {
        term = Normalizer.normalize(term, Normalizer.Form.NFD);
        return ASCII_PATTERN.matcher(term).replaceAll("").toLowerCase();
    }

}
