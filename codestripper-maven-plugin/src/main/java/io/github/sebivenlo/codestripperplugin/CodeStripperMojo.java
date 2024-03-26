package io.github.sebivenlo.codestripperplugin;

import codestripper.CodeStripper;
import mytinylogger.LoggerLevel;
import codestripper.PathLocations;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
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

        LoggerWrapper log = new LoggerWrapper( getLog(), verbosity );
        log.level( verbosity );
        try {
            log.info( () -> "start code stripping." );
            var outDir = Files.createDirectories( Path.of( System.getProperty(
                    "user.dir" ) ).resolve( outDirS ) );
            PathLocations locations = new PathLocations( log, outDir );
            var stripper = new CodeStripper.Builder()
                    .logger( log )
                    .pathLocations( locations )

                    .dryRun( dryRun )
                    .extraResources( extraResources )
                    .build();

            Path resultingProject = stripper.strip();
        } catch ( IOException ex ) {
            log.error( () -> ex.getMessage() );
        }
    }

    /**
     * Stripper tag to use. Defaults to 'cs'.
     *
     */
    @Parameter( property = "codestripper.tag", defaultValue = "cs" )
    protected String tag;

    /**
     * Output directory. Defaults to 'target/stripper-out'.
     */
    @Parameter( property = "codestripper.outDir",
            defaultValue = "target/stripper-out" )
    protected String outDirS;

    /**
     * Verbosity. Defaults to INFO. More verbose is DEBUG or FINE.
     */
    @Parameter( property = "codestripper.verbosity", defaultValue = "FINE" )
    protected LoggerLevel verbosity;

    /**
     * Do not write results.
     */
    @Parameter( property = "codestripper.dryRun", defaultValue = "false" )
    protected boolean dryRun;

    /**
     * working directory. Defaults to "${project.build.directory}".
     */
    @Parameter( property = "codestripper.working-directory",
            defaultValue = "${project.build.directory}" )
    protected String workDir = System.getProperty( "user.dir" );

    /**
     * Extra resources not with the current dir or its children to include in
     * the zips.
     */
    @Parameter( property = "extraResources" )
    protected List<String> extraResources = List.of();

    /**
     * Used by maven framework.
     */
    public CodeStripperMojo() {
    }

}
