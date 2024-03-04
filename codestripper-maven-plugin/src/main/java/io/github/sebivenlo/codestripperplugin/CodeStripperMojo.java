package io.github.sebivenlo.codestripperplugin;

import codestripper.CodeStripper;
import java.io.IOException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

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
            new CodeStripper( getLog() ).strip( "" );
            getLog().info( "stripped code, result in target/stripper-out" );
        } catch ( IOException ex ) {
            getLog().error( ex.getMessage(), ex );
        }
    }
//
//    @Parameter( property = "codestripper.includeGlob", defaultValue = "*.java" )
//    protected String includeGlob;
//
//    @Parameter( property = "codestripper.commentToken", defaultValue = "//" )
//    protected String commentToken;
//
//    @Parameter( property = "codestripper.tag", defaultValue = "cs" )
//    protected String tag;
//
//    @Parameter( property = "codestripper.outDir",
//            defaultValue = "target/stripped" )
//    protected String outDir;
//
//    @Parameter( property = "codestripper.verbosity", defaultValue = "WARNING" )
//    protected String verbosity;
//
//    @Parameter( property = "codestripper.dryRun", defaultValue = "false" )
//    protected String dryRun;
//
//    @Parameter( property = "codestripper.working-directory", defaultValue = "" )
//    protected String workDir = "";

    /**
     * Used by maven framework.
     */
    public CodeStripperMojo() {
    }

}
