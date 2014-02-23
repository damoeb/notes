package org.notes.plugin.tf;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

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

        WikiDumpParser parser = new WikiDumpParser(minTermLength, maxTermLength, pathToWikiDumpXml, logOnDocCount, getLog());

        Outputter outputter = new Outputter(outputFileName, minTermOccurrences, getLog());

        outputter.output(parser.parse());

        getLog().info("Finished");
    }
}