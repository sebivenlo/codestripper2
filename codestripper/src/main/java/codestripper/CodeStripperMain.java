package codestripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import mytinylogger.Logger;
import mytinylogger.LoggerLevel;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Starter for CLI version.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
@Command( name = "codestripper", mixinStandardHelpOptions = true,
          version = "codestripper 0.4",
          description = "Strips code and packages assignment and solution." )
public class CodeStripperMain implements Callable<Integer> {

    @Option( names = { "-x", "--extras" },
             description = "extra files to add not within this directory or its children",
             arity = "1..*"
    )
    private List<String> extras = List.of();

    @Option( names = { "-v", "--verbosity" },
             description = "Level of detail output.",
             defaultValue = "INFO" )
    LoggerLevel verbosity = LoggerLevel.INFO;

    @Option( names = { "-b", "--basedir" },
             description = "project base directory" )
    String baseDir = System.getProperty( "user.dir" );

    @Option( names = { "-h", "--help" }, usageHelp = true,
             description = "display a help message" )
    private boolean helpRequested = false;

    public Integer call() throws Exception {
        if ( helpRequested ) {
            return 0;
        }
        Path outDir = Path.of( baseDir )
                .resolve( "target" )
                .resolve( "stripper-out" );
        Files.createDirectories( outDir );
        Logger logger = new Logger().level( verbosity );
        PathLocations locations = new PathLocations( logger, outDir );
        CodeStripper codeStripper
                = new CodeStripper.Builder()
                        .pathLocations( locations )
                        .logger( logger )
                        .extraResources( extras )
                        .build();

        for ( String s : locations.toString()
                .split( "\n" ) ) {
            logger.info( () -> "config " + s );
        }
        logger.info( () -> "config stripped project [\033[32m" + locations
                .strippedProject() + "\033[m]" );
        codeStripper.strip();

        return 0;
    }

    /**
     * Entry of program.
     *
     * @param args not used
     * @throws IOException should not occur.
     * @throws java.lang.InterruptedException for the impatient.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        int exitCode = new CommandLine( new CodeStripperMain() ).execute( args );
        System.exit( exitCode );
    }

    /**
     * No instances.
     */
    private CodeStripperMain() {
    }

}
