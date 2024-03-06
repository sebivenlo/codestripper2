package codestripper;

import static codestripper.LoggerLevel.DEBUG;
import static codestripper.LoggerLevel.FINE;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import streamprocessor.ProcessorFactory;

/**
 * The work horse of the code stripper.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class CodeStripper {

    public static final String DEFAULT_OUTDIR = "target/stripper.out";

    private final Log log;
    private final boolean dryRun;

    /**
     * Do the work starting at the root.
     *
     * @param root of the stripping action.
     *
     * @throws IOException hop to die
     */
    public final void strip(Path root) throws IOException {
        Instant start = Instant.now();
        try ( Zipper solution = new Zipper( Path.of( "target", "solution.zip" ) ); //
                 Zipper assignment = new Zipper( Path.of( "target",
                        "/assignment.zip" ) ); ) {
            processTextFiles( root, solution, assignment );
            archiver.addBinaryFiles( root );//processBinaryFiles( root, solution, assignment );
            archiver.addExtras( root, extraResources );//addExtraResources( root, solution, assignment );
        } catch ( Exception ex ) {
            Logger.getLogger( CodeStripper.class.getName() )
                    .log( Level.SEVERE, null, ex );
        }
        // save file names for later zipping.
        Instant end = Instant.now();

        Duration took = Duration.between( start, end );
        log.info(
                "codestripper processed " + fileCount + " files in " + took
                        .toMillis() + " milliseconds" );
    }

    Path target = Path.of( "target" );
    Path dotgit = Path.of( ".git" );
    int fileCount = 0;

    /**
     * Process the files in the root directory. Typically this is the directory
     * that contains the maven pom file.
     *
     * @param root directory of the maven project
     * @param target to not visit
     * @param dotgit ignore
     * @param solution zip container for solution files
     * @param assignment zip container for assignment files
     * @throws IOException should not occur.
     */
    void processTextFiles(Path root,
            final Zipper solution, final Zipper assignment) throws IOException {

        Files.walk( root, Integer.MAX_VALUE )
                .filter( f -> !Files.isDirectory( f ) )
                //                .filter( f -> !f.startsWith( out ) )
                .filter( f -> !f.startsWith( target ) )
                .filter( f -> !f.startsWith( dotgit ) )
                .filter( Archiver::isText )
                .filter( f -> !f.getFileName().toString().endsWith( "~" ) )
                .sorted()
                .forEach( file -> process( file, solution, assignment ) );
    }

    private final Path outDir;

    private void process(Path javaFile, Zipper solution, Zipper assignment) {
        fileCount++;
        logDebug( () -> "start stripping file " + javaFile.toString() );
        var factory = new ProcessorFactory( javaFile ).logLevel( this.logLevel );
        try {
            var lines = Files.lines( javaFile ).toList();
            // unprocessed files go to solution
            archiver.addSolutionLines( javaFile, lines );
            List<String> result = lines.stream()
                    .map( factory::apply )
                    .flatMap( x -> x ) // flatten the result
                    .toList();

            if ( !dryRun && !result.isEmpty() ) {
                // add to assigmnet after processing
                logDebug( () -> "added " + javaFile.toString() );
                archiver.addAssignmentLines( javaFile, lines );
            }
        } catch ( IOException ex ) {
            log.error( ex.getMessage() );
        }

        if ( factory.hasDanglingTag() ) {
            log.warn(
                    "file " + javaFile.toString() + " has dangling tag, started at " + factory
                    .danglingTag() );
        }
        logDebug( () -> "completed stripping " + javaFile.toString() );
    }

    /**
     * Who doesn't need me.
     *
     * @param args to ignore
     * @throws IOException hope to die
     */
    public static void main(String... args) throws IOException {
        new CodeStripper( new SystemStreamLog(), Path
                .of( DEFAULT_OUTDIR ) ).strip( Path.of( "" ) );
    }

    /**
     * No specialties needed.
     *
     * @param log to set
     * @param dryRun flag
     */
    public CodeStripper(Log log, boolean dryRun, Path outDir) {
        this.dryRun = dryRun;
        this.log = log;
        this.outDir = outDir;
        archiver = new Archiver( outDir.toString(), log );
    }

    private final Archiver archiver;

    /**
     * Default stripper with dryRun false;
     *
     * @param log
     * @param outDir for results.
     */
    public CodeStripper(Log log, Path outDir) {
        this( log, false, outDir );
    }
    private LoggerLevel logLevel = FINE;

    /**
     * Set the logging level.
     *
     * @param level
     */
    public CodeStripper logLevel(LoggerLevel level) {
        this.logLevel = level;
        return this;
    }

    void logFine(Supplier<String> msg) {
        if ( this.logLevel.compareTo( FINE ) >= 0 ) {
            log.info( msg.get() );
        }
    }

    void logDebug(Supplier<String> msg) {
        if ( this.logLevel.compareTo( DEBUG ) >= 0 ) {
            log.info( msg.get() );
        }
    }

    private List<String> extraResources = List.of();

    public CodeStripper extraResources(List<String> resources) {
        this.extraResources = resources;
        return this;
    }

}
