package io.github.sebivenlo.codestripperplugin;

import codestripper.CodeStripper;
import codestripper.LoggerLevel;
import java.io.IOException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Maven plugin entry for the code stripper plugin.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
@Mojo( name = "strip",
        defaultPhase = LifecyclePhase.NONE )
public class CodeStripperMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            getLog().info( "start code stripping." );
            new CodeStripper( getLog(), dryRun ).logLevel( verbosity ).strip(
                    workDir );
            getLog().info( "stripped code, result in " + outDir );
        } catch ( IOException ex ) {
            getLog().error( ex.getMessage(), ex );
        }
    }
//    all files in the solution directory undergo processing.
//    @Parameter( property = "codestripper.includeGlob", defaultValue = "*.java" )
//    protected String includeGlob;

    // This value is actually inferred from the file extension.
//    @Parameter( property = "codestripper.commentToken", defaultValue = "//" )
//    protected String commentToken;
//
    @Parameter( property = "codestripper.tag", defaultValue = "cs" )
    protected String tag;
//
    @Parameter( property = "codestripper.outDir",
            defaultValue = "target/stripper-out" )
    protected String outDir = "target/stripper-out";
//
    @Parameter( property = "codestripper.verbosity", defaultValue = "INFO" )
    protected LoggerLevel verbosity;
//
    @Parameter( property = "codestripper.dryRun", defaultValue = "false" )
    protected boolean dryRun;
//
    @Parameter( property = "codestripper.working-directory", defaultValue = "" )
    protected String workDir = "";

    /**
     * Used by maven framework.
     */
    public CodeStripperMojo() {
    }

}
