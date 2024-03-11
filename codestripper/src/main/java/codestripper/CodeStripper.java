package codestripper;

import static codestripper.LoggerLevel.DEBUG;
import static codestripper.LoggerLevel.FINE;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
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
public final class CodeStripper implements ChippenDale {

    static String fileSep = System.getProperty( "file.seperator", "/" );
    /**
     * Default out dir.
     */
    public static final Path DEFAULT_STRIPPER_OUTDIR = Path.of( "target",
            "stripper-out" );

    private final boolean dryRun;
    private Log logger;
//    private final Path outDirPath;
//    private Path outDir = null;
    LoggerLevel logLevel = LoggerLevel.INFO;

    /**
     * Do the work starting at the root.
     *
     * @param root of the stripping action.
     * @return the directory containing the stripped project.
     *
     * @throws IOException hop to die
     */
    public final Path strip(Path root) throws IOException {
        Instant start = Instant.now();
        Objects.requireNonNull( logger );
        Path projectDir = null;
        try ( Archiver archiver = new Archiver( logger, locations ); ) {
            projectDir = archiver.projectDir();
            processTextFiles( root, archiver );
            logger.info( " adding non stripables" );
            archiver.addAssignmentFiles( root );
            logger.info( " adding extras" );
            archiver.addExtras( extraResources );
        } catch ( Exception ex ) {
            logger.error( ex.getMessage() );
        } finally {
            // save file names for later zipping.
            Instant end = Instant.now();

            Duration took = Duration.between( start, end );
            logger.info(
                    "codestripper processed " + fileCount + " files in " + took
                            .toMillis() + " milliseconds" );
        }

        return projectDir;
    }

    int fileCount = 0;

    @Override
    public String projectName() {
        return pwd.getFileName().toString();
    }

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
                .filter( f -> acceptablePath( f, locations.out() ) )
                .filter( ChippenDale::isText )
                .forEach( file -> process( file, archiver ) );
    }

    private void process(Path javaFile, Archiver archiver) {
        fileCount++;
        logDebug( () -> "start stripping file " + javaFile.toString() );
        var factory = new ProcessorFactory( javaFile, logger ).logLevel(
                LoggerLevel.FINE );
        try {
            var lines = Files.lines( javaFile ).toList();
            // unprocessed files go to solution
            archiver.addSolutionLines( javaFile, lines );
            List<String> stripped = lines.stream()
                    .map( factory::apply )
                    .flatMap( x -> x ) // flatten the result
                    .toList();

            if ( !dryRun && !stripped.isEmpty() ) {
                // add to assigmnet after processing
                logDebug( () -> "added stripped file" + javaFile.toString() );
                archiver.addAssignmentLines( javaFile, stripped );
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
    private CodeStripper(Log logger, boolean dryRun, PathLocations locs) throws IOException {
        this.logger = logger;
        this.locations = locs;
        this.dryRun = dryRun;
    }
    final PathLocations locations;

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

//    final Path outDir() {
//        if ( null == this.outDir ) {
//            try {
//                if ( !outDirPath.toRealPath().toFile().exists() ) {
//                    this.outDir = Files.createDirectories( outDirPath
//                            .toAbsolutePath() );
//                }
//            } catch ( IOException ex ) {
//                Logger.getLogger( ChippenDale.class.getName() )
//                        .log( Level.SEVERE, null, ex );
//                ex.printStackTrace();
//            }
//        }
//        return outDir;
//    }
    public static class Builder {

        // sensible defaults.
        private boolean dryRun = false;
        private List<String> extraResources = List.of();
        private Path outDir = DEFAULT_STRIPPER_OUTDIR;
        private PathLocations locations;
        private Log logger = new SystemStreamLog();

        public Builder dryRun(boolean dryRun) {
            this.dryRun = dryRun;
            return this;
        }

        public Builder extraResources(List<String> extraResources) {
            this.extraResources = extraResources;
            return this;
        }

        public Builder pathLocations(PathLocations locations) {
            this.locations = locations;
            return this;
        }

        public Builder logger(Log logger) {
            this.logger = logger;
            return this;
        }

        CodeStripper build() {
            CodeStripper result = null;
            try {
                result = new CodeStripper( logger, dryRun, locations )
                        .extraResources(
                                extraResources );
            } catch ( IOException ex ) {
                this.logger.error( ex.getMessage() );
                throw new RuntimeException( ex );
            }
            return result;
        }

    }

}
