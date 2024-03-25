package codestripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import mytinylogger.DefaultLogger;
import loggerwrapper.Logger;
import loggerwrapper.LoggerLevel;
import picocli.CommandLine.Option;

/**
 * Starter for CLI version.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class CodeStripperMain {

    @Option( names = { "-x", "--extras" },
             description = "extra files to add not within this directory or its children" )
    private List<String> extras = List.of();

    @Option( names = { "-v", "--verbosity" },
             description = "Level of detail output." )
    LoggerLevel verbosity = LoggerLevel.INFO;

    @Option( names = { "-b", "--basedir" },
             description = "project base directory" )
    String baseDir = System.getProperty( "user.dir" );

    /**
     * Entry of program.
     *
     * @param args not used
     * @throws IOException should not occur.
     * @throws java.lang.InterruptedException for the impatient.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        Path outDir = Path.of( System.getProperty( "user.dir" ) )
                .resolve(
                        "target" )
                .resolve( "stripper-out" );
        Files.createDirectories( outDir );
        var verb = System.getProperty( "codestripper.verbosity", "INFO" );
        var verbosity = LoggerLevel.INFO;
        try {
            verbosity = LoggerLevel.valueOf( verb );
        } catch ( Throwable ignored ) {
        }
        Logger logger = new DefaultLogger().level( verbosity );
        PathLocations locations = new PathLocations( logger, outDir );
        CodeStripper codeStripper
                = new CodeStripper.Builder()
                        .pathLocations( locations )
                        .logger( logger )
                        .extraResources( List.of( "../README.md", "../images" ) )
                        .build();

        for ( String s : locations.toString()
                .split( "\n" ) ) {
            logger.info( () -> "config " + s );
        }
        logger.info( () -> "config stripped project [\033[32m" + locations
                .strippedProject() + "\033[m]" );
        codeStripper.strip();
    }

    /**
     * No instances.
     */
    private CodeStripperMain() {
    }

}
