/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package codestripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import mytinylogger.DefaultLogger;
import loggerwrapper.Logger;
import loggerwrapper.LoggerLevel;

/**
 * Starter for CLI version.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class CodeStripperMain {

    /**
     * Entry of program.
     *
     * @param args not used
     * @throws IOException should not occur.
     * @throws java.lang.InterruptedException for the impatient.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        Path outDir = Path.of( System.getProperty( "user.dir" ) ).resolve(
                "target" ).resolve( "stripper-out" );
        Files.createDirectories( outDir );
        Logger logger = new DefaultLogger().level( LoggerLevel.FINE );
        PathLocations locations = new PathLocations( logger, outDir );
        CodeStripper codeStripper
                = new CodeStripper.Builder()
                        .pathLocations( locations )
                        .logger( logger )
                        .extraResources( List.of( "../README.md", "../images" ) )
                        .build();

        for ( String s : locations.toString().split( "\n" ) ) {
            logger.info( () -> "config " + s );
        }
        logger.info( () -> "config stripped project [\033[32m" + locations
                .strippedProject() + "\033[m]" );
        codeStripper.strip();
        var checker = new StrippedCodeValidator( logger, locations );
        try {
            checker.validate();
        } catch ( CodeStripperValidationException ex ) {
            logger.error( () -> ex.getLongMessage() );
        }
        logger.info( () -> "Hello World!" );
    }

    /**
     * No instances.
     */
    private CodeStripperMain() {
    }

}
