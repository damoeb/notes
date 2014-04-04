package org.notes.plugin.tf;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.maven.plugin.logging.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.notes.common.domain.TermFrequency;
import org.notes.common.tokenizer.Language;
import org.notes.common.tokenizer.TokenStreamProvider;
import org.notes.common.utils.TextUtils;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

public class WikiDumpParser {

    private static final int MAX_ELEMENTS = 0;

    private static final Pattern URL_PATTERN = Pattern.compile("http[s]?://[^ ]+");

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
    private final Language lang;

    // ------------

    private long documentCount = 0;
    private TokenStreamProvider streamProvider;

    public WikiDumpParser(Integer minTermLength, Integer maxTermLength, String pathToWikiDumpXml, Integer logOnDocCount, Integer stopAfterDocCount, Language lang, Log log) {
        this.minTermLength = minTermLength;
        this.maxTermLength = maxTermLength;
        this.pathToWikiDumpXml = pathToWikiDumpXml;
        this.logOnDocCount = logOnDocCount;
        this.stopAfterDocCount = stopAfterDocCount;
        this.lang = lang;
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


        termsInDocument.clear();

        TokenStream tokenStream = null;

        try {

            tokenStream = getStreamProvider().getTokenizer(text, getLang());

            CharTermAttribute charTermAttr = tokenStream.getAttribute(CharTermAttribute.class);
            tokenStream.reset();

            while (tokenStream.incrementToken()) {
                final String charTerm = charTermAttr.toString();

                if (charTerm.length() < minTermLength) {
                    getLog().debug("'" + charTerm + "' too short");
                    continue;
                }

                if (charTerm.length() > maxTermLength) {
                    getLog().debug("'" + charTerm + "' too long");
                    continue;
                }

                termsInDocument.put(TextUtils.normedTerm(charTerm), charTerm);
            }

            tokenStream.end();

        } catch (IOException e) {
            getLog().error(e);
        } finally {
            if (tokenStream != null) {
                try {
                    tokenStream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        for (String normed : termsInDocument.keySet()) {
            pushTerm(normed, termsInDocument.get(normed));
        }
    }

    public Language getLang() {
        return lang;
    }

    private TokenStreamProvider getStreamProvider() {
        if (streamProvider == null) {
            streamProvider = new TokenStreamProvider();
        }

        return streamProvider;
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
}
