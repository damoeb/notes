package org.notes.plugin.tf;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class RawOutputter implements Outputter {

    private final Log log;
    private final String outputFileName;
    private final Integer minTermOccurrences;

    public RawOutputter(String outputFileName, Integer minTermOccurrences, Log log) {
        this.outputFileName = outputFileName;
        this.minTermOccurrences = minTermOccurrences;
        this.log = log;
    }

    @Override
    public Log getLog() {
        return log;
    }

    @Override
    public void output(ParserResult result) {


        getLog().info("------------------------------------------------------------------------");
        getLog().info("Outputting results");
        getLog().info("------------------------------------------------------------------------");
        getLog().info("");

        getLog().info(result.getDocumentCount() + " documents parsed");

        new File("target").mkdir();

        try (PrintStream out = new PrintStream(new File("target/" + outputFileName))) {

            for (TermFrequency t : result.getTerms()) {

                if (NumberUtils.isNumber(t.getTerm())) {
                    continue;
                }

                if (t.getFrequency() < minTermOccurrences) {
                    continue;
                }
                out.println(t.getTerm() + " " + t.getFrequency());
            }

            out.println(result.getDocumentCount() + " documents");

            out.flush();
            out.close();

        } catch (FileNotFoundException e) {
            getLog().error(e.getMessage());
        }
    }
}
