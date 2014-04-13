package org.notes.plugin.harvest;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "import-mediathek", threadSafe = false, requiresOnline = false, defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class ImportMediathekGoal extends AbstractMojo {

    /**
     * sourceXml
     */
    @Parameter(required = true)
    private String sourceXml;

    /**
     * username
     */
    @Parameter(required = true)
    private String username;

    /**
     * password
     */
    @Parameter(required = true)
    private String password;

    /**
     * abortAfterDocCount
     */
    @Parameter
    private Integer abortAfterDocCount;

    public ImportMediathekGoal() {
        // default
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        getLog().info("sourceXml " + sourceXml);
        getLog().info("username " + username);
//        getLog().info("password " + );
        getLog().info("abortAfterDocCount: " + abortAfterDocCount);


        getLog().info("Finished");
    }
}