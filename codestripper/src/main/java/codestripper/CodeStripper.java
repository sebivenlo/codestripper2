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
import org.slf4j.Logger;
import streamprocessor.ProcessorFactory;

/**
 * The work horse of the code stripper.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public final class CodeStripper extends ChippenDale {

    /**
     * Default out dir.
     */
    public static final String DEFAULT_STRIPPER_OUTDIR = "target/stripper.out";

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
        try ( Archiver archiver = new Archiver( outDir(), logger ); ) {
            processTextFiles( root, archiver );
            archiver.addAssignmentFiles( root );
            archiver.addExtras( extraResources );
        } catch ( Exception ex ) {
            logger.error( ex.getMessage() );
        }
        // save file names for later zipping.
        Instant end = Instant.now();

        Duration took = Duration.between( start, end );
        logger.info(
                "codestripper processed " + fileCount + " files in " + took
                        .toMillis() + " milliseconds" );
    }

    int fileCount = 0;

    /**
     * Process the files in the root directory. Typically this is the directory
     * that contains the maven pom file.
     *
     * @param root directory of the maven project
     * @param target to not visit
     * @param dotgit ignore
     * @throws IOException should not occur.
     */
    void processTextFiles(Path root, Archiver archiver) throws IOException {

        Files.walk( root, Integer.MAX_VALUE )
                .filter( this::acceptablePath )
                .filter( this::isText )
                .sorted()
                .forEach( file -> process( file, archiver ) );
    }

    private void process(Path javaFile, Archiver archiver) {
        fileCount++;
        logDebug( () -> "start stripping file " + javaFile.toString() );
        var factory = new ProcessorFactory( javaFile ).logLevel( LoggerLevel.FINE );
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
                logDebug( () -> "added stripped file" + javaFile.toString() );
                archiver.addAssignmentLines( javaFile, result );
            }
        } catch ( IOException ex ) {
            logger.error( ex.getMessage() );
        }

        if ( factory.hasDanglingTag() ) {
            logger.warn(
                    "file " + javaFile.toString() + " has dangling tag, started at " + factory
                    .danglingTag() );
        }
        logDebug( () -> "completed stripping " + javaFile.toString() );
    }

    /**
     * No specialties needed.
     *
     * @param logger to set
     * @param dryRun flag
     * @param outDir for action results.
     * @throws java.io.IOException should not occur.
     */
    public CodeStripper(Logger logger, boolean dryRun, Path outDir) throws IOException {
        super( logger, outDir );
        this.dryRun = dryRun;
    }

    /**
     * Default stripper with dryRun false;
     *
     * @param log to use
     * @param outDir for results.
     * @throws java.io.IOException should not occur
     */
    public CodeStripper(Logger log, Path outDir) throws IOException {
        this( log, false, outDir );
    }

    /**
     * Set the logging level.
     *
     * @param level logging level
     * @return this
     */
    public CodeStripper logLevel(LoggerLevel level) {
        this.logLevel = level;
        return this;
    }

    void logFine(Supplier<String> msg) {
        if ( this.logLevel.compareTo( FINE ) >= 0 ) {
            logger.info( msg.get() );
        }
    }

    void logDebug(Supplier<String> msg) {
        if ( this.logLevel.compareTo( DEBUG ) >= 0 ) {
            logger.info( msg.get() );
        }
    }

    private List<String> extraResources = List.of();

    /**
     * Add extra resources.
     *
     * @param resources to add
     * @return this
     */
    public CodeStripper extraResources(List<String> resources) {
        this.extraResources = resources;
        return this;
    }
}
