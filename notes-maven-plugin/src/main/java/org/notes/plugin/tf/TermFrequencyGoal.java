package org.notes.plugin.tf;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.regex.Pattern;

@Mojo(name = "term-frequency", threadSafe = false, requiresOnline = false, defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class TermFrequencyGoal extends AbstractMojo {

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
    private Integer minTermOccurrences = 4;

    /**
     * pathToWikiDumpXml
     */
    @Parameter(required = true)
    private String pathToWikiDumpXml;

    /**
     * outputFileName
     */
    @Parameter(defaultValue = "TermFrequency.out")
    private String outputFileName;

    /**
     * outputType
     */
    @Parameter(defaultValue = "sql", required = true)
    private String outputType;

    /**
     * abortAfterDocCount
     */
    @Parameter
    private Integer abortAfterDocCount;

    /**
     * logOnDocCount
     */
    @Parameter(defaultValue = "100000")
    private Integer logOnDocCount;
    private Outputter outputterByType;

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
        getLog().info("abortAfterDocCount: " + abortAfterDocCount);

        Output output = Output.fromString(outputType);
        if (output == null) {
            throw new IllegalArgumentException(String.format("Invalid outputType '%s'. Valid ones are %s", outputType, StringUtils.join(Output.values(), ", ")));
        }
        getLog().info("outputType: " + outputType);

        WikiDumpParser parser = new WikiDumpParser(minTermLength, maxTermLength, pathToWikiDumpXml, logOnDocCount, abortAfterDocCount, getLog());

        Outputter outputter = getOutputterByType(output);

        outputter.output(parser.parse());

        getLog().info("Finished");
    }

    public Outputter getOutputterByType(Output type) {
        switch (type) {
            case SQL:
                return new SqlOutputter(outputFileName, minTermOccurrences, getLog());
            default:
            case RAW:
                return new RawOutputter(outputFileName, minTermOccurrences, getLog());
        }
    }
}