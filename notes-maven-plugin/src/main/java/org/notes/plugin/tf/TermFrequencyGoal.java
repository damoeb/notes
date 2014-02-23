package org.notes.plugin.tf;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

@Mojo(name = "term-frequency", threadSafe = true, requiresOnline = false, defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class TermFrequencyGoal extends AbstractMojo {

    private static final int MAX_ELEMENTS = 0;

    private final Pattern urlPattern = Pattern.compile("http[s]?://[^ ]+");

    /**
     * minTermLength
     */
    @Parameter
    private Integer minTermLength = 2;

    /**
     * maxTermLength
     */
    @Parameter
    private Integer maxTermLength = 40;

    /**
     * minTermOccurrences
     */
    @Parameter
    private Integer minTermOccurrences = 2;

    /**
     * pathToWikiDumpXml
     */
    @Parameter(required = true)
    private String pathToWikiDumpXml;

    /**
     * outputFileName
     */
    @Parameter(defaultValue = "TermFrequency.txt")
    private String outputFileName;

    /**
     * logOnDocCount
     */
    @Parameter(defaultValue = "100000")
    private Integer logOnDocCount;

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

    private final Set<String> termsInDocument = new HashSet<>(1000);
    private long documentCount = 0;

    public TermFrequencyGoal() {
        // default
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        getLog().info("minTermLength: " + minTermLength);
        getLog().info("maxTermLength: " + maxTermLength);
        getLog().info("minTermOccurrences: " + minTermOccurrences);
        getLog().info("pathToWikiDumpXml: " + pathToWikiDumpXml);
        getLog().info("outputFileName: " + outputFileName);

        getLog().info("processing (this can take a while)");

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

        } catch (Exception e) {
            getLog().error(e);

        } finally {

            persist(start);

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

    private void persist(long start) {

        getLog().info("write to file");
        getLog().info(documentCount + " documents parsed");

        new File("target").mkdir();

        try (PrintStream out = new PrintStream(new File("target/" + outputFileName))) {

            for (TermFrequency t : sortedTerms) {

                if (NumberUtils.isNumber(t.getTerm())) {
                    continue;
                }

                if (t.getFrequency() < minTermOccurrences) {
                    continue;
                }
                out.println(t.getTerm() + " " + t.getFrequency());
            }

            out.println(documentCount);

            out.flush();
            out.close();

            getLog().info("Time " + (System.currentTimeMillis() - start) / 1000d + "s");

        } catch (FileNotFoundException e) {
            getLog().error(e.getMessage());
        }
    }


    private String cleanUrls(String plaintext) {
        return urlPattern.matcher(plaintext).replaceAll("");
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

        documentCount++;

        final StringTokenizer tokenizer = new StringTokenizer(text, " &\\_\"<>|!?=+–~-*/„“()’`´_#'°^@€%$§[]{}\n\t :,;.ˈ¹−…₂»«%¬”‘·∴ʿ");

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

            termsInDocument.add(token);
        }

        for (String term : termsInDocument) {
            pushTerm(term);
        }
    }

    private void pushTerm(final String term) throws InterruptedException {

        if (termFreqMap.containsKey(term)) {

            TermFrequency tf = termFreqMap.get(term);

            sortedTerms.remove(tf);
            tf.incrementFrequency();
            sortedTerms.add(tf);

        } else {

            TermFrequency tf = new TermFrequency(term);
            termFreqMap.put(term, tf);
            sortedTerms.add(tf);
        }
    }

}