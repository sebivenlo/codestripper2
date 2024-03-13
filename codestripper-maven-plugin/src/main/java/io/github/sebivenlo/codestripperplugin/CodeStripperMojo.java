package io.github.sebivenlo.codestripperplugin;

import codestripper.CodeStripper;
import codestripper.LoggerLevel;
import codestripper.PathLocations;
import java.io.IOException;
import java.nio.file.Files;
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
            var out = Files.createDirectories( Path.of( System.getProperty(
                    "user.dir" ), "target", "stripper-out" ) );

            PathLocations locations = new PathLocations( getLog(), out );
            var stripper = new CodeStripper.Builder()
                    .logger( log )
                    .pathLocations( locations )
                    .dryRun( dryRun )
                    .extraResources( List.of() )
                    .build();
            Path resultingProject
                    = stripper.strip( Path.of( workDir ) );
            getLog().info( "stripped code, result in " + resultingProject );
            var checker = new StrippedCodeValidator( log, locations );
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

    @Parameter( property = "codestripper.tag", defaultValue = "cs" )
    protected String tag;
//
    @Parameter( property = "codestripper.outDir",
            defaultValue = "target/stripper-out" )
    protected String outDir;
//
    @Parameter( property = "codestripper.verbosity", defaultValue = "INFO" )
    protected LoggerLevel verbosity;
//
    @Parameter( property = "codestripper.dryRun", defaultValue = "false" )
    protected boolean dryRun;
//
    @Parameter( property = "codestripper.working-directory",
            defaultValue = "${project.build.directory}" )
    protected String workDir = System.getProperty( "user.dir" );

    @Parameter( property = "extraResources", defaultValue = "false" )
    protected List<String> extraResources = List.of();

    /**
     * Used by maven framework.
     */
    public CodeStripperMojo() {
    }

}
