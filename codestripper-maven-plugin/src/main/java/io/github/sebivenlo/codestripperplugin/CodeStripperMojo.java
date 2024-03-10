package io.github.sebivenlo.codestripperplugin;

import codestripper.CodeStripper;
import codestripper.LoggerLevel;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Strips the text files in the project directory based on stripper tags.
 *
 *
 * <pre> @{code Set.of( "java", "sql", "txt", "sh", "yaml", "yml" )}</pre>
 *
 * All text files are stripped in all subdirectories.
 *
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
@Mojo( name = "strip",
        defaultPhase = LifecyclePhase.NONE )
public class CodeStripperMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Log log = getLog();
        try {
            log.info( "start code stripping." );
            var stripper = new CodeStripper( getLog(), dryRun,
                    Path.of( outDir ) )
                    .extraResources( extraResources )
                    .logLevel( verbosity );
            Path resultingProject
                    = stripper.strip( Path.of( workDir ) );
            getLog().info( "stripped code, result in " + resultingProject );
            var checker = new StrippedCodeValidator( resultingProject, log );
            checker.validate();
        } catch ( IOException ex ) {
            getLog().error( ex.getMessage(), ex );
        } catch ( InterruptedException ex ) {
            Logger.getLogger( CodeStripperMojo.class.getName() )
                    .log( Level.SEVERE, null, ex );
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
            defaultValue = CodeStripper.DEFAULT_STRIPPER_OUTDIR )
    protected String outDir = CodeStripper.DEFAULT_STRIPPER_OUTDIR;
//
    @Parameter( property = "codestripper.verbosity", defaultValue = "INFO" )
    protected LoggerLevel verbosity;
//
    @Parameter( property = "codestripper.dryRun", defaultValue = "false" )
    protected boolean dryRun;
//
    @Parameter( property = "codestripper.working-directory", defaultValue = "" )
    protected String workDir = "";

    @Parameter( property = "extraResources", defaultValue = "" )
    protected List<String> extraResources = List.of();

    /**
     * Used by maven framework.
     */
    public CodeStripperMojo() {
    }

}
