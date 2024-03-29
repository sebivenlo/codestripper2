package codestripper;

import mytinylogger.LoggerLevel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import mytinylogger.Logger;

import streamprocessor.ProcessorFactory;

/**
 * The work horse of the code stripper.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public final class CodeStripper {

    /**
     * Default out dir.
     */
    private final boolean dryRun;
    private final Logger logger;

    /**
     * Do the work starting at the root.
     *
     * @return the directory containing the stripped project.
     *
     * @throws IOException hop to die
     */
    public final Path strip() throws IOException {
        Path root = locations.work();
        Instant start = Instant.now();
        Objects.requireNonNull( logger );
        try ( Archiver archiver = new Archiver( logger, locations ); ) {
            processTextFiles( root, archiver );
            logger.debug( () -> "adding non-stripables" );
            archiver.addAssignmentFiles( root );
            logger.debug( () -> "adding extras" );
            archiver.addExtras( extraResources );
        } catch ( Exception ex ) {
            logger.error( () -> ex.getMessage() );
        } finally {
            // save file names for later zipping.
            Instant end = Instant.now();

            Duration took = Duration.between( start, end );
            if ( fileCount < 1 ) {
                logger.warn( ()
                        -> "No files were selected for stripping, please verify your configuration" );
                for ( String line : locations.toString().split( "\n" ) ) {
                    logger.warn( () -> line );
                }
            } else {
                logger.info( () -> "codestripper processed "
                        + fileCount + " files in " + took
                                .toMillis() + " milliseconds" );
            }
        }

        return locations.strippedProject();
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
                .filter( f -> locations.acceptablePath( f ) )
                .filter( ChippenDale::isText )
                .forEach( file -> process( file, archiver ) );
    }

    private void process(Path javaFile, Archiver archiver) {
        fileCount++;
        try ( var factory = new ProcessorFactory( javaFile, logger ); ) {
            var lines = Files.lines( javaFile ).toList();
            // unprocessed files go to solution
            archiver.addSolutionLines( javaFile, lines );
            List<String> stripped = lines.stream()
                    .map( factory::apply )
                    .flatMap( x -> x ) // flatten the result
                    .toList();

            if ( factory.hasDanglingTag() ) {
                logger.warn( ()
                        -> "file \033[33m" + locations.work().relativize(
                                javaFile ).toString() + "\033[m has dangling tags:  " );
                String danglingTags = factory.danglingTags();
                for ( String s : danglingTags.split( "\n" ) ) {
                    logger.warn( () -> s );
                }

            }
            if ( !dryRun && !stripped.isEmpty() ) {
                // add to assignment after processing
                logger.debug( () -> "added stripped file \033[36m" + locations
                        .workRelative( javaFile ).toString() + "\033[m" );
                archiver.addAssignmentLines( javaFile, stripped );
            }
        } catch ( IOException ex ) {
            logger.error( () -> ex.getMessage() );
        } catch ( Exception ex ) {
            logger.error( () -> ex.getMessage() );
        }

    }

    /**
     * No specialties needed.
     *
     * @param logger to set
     * @param dryRun flag
     * @param outDir for action results.
     * @throws java.io.IOException should not occur.
     */
    private CodeStripper(Logger logger, boolean dryRun,
            PathLocations locs) throws IOException {
        this.logger = logger;
        this.locations = locs;
        this.dryRun = dryRun;
    }
    final PathLocations locations;

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

    /**
     * Build a stripper.
     */
    public static class Builder {

        /**
         * Java config doc wants a comment.
         */
        public Builder() {
        }

        // sensible defaults.
        private boolean dryRun = false;
        private List<String> extraResources = List.of();
        private PathLocations locations;
        private Logger logger = null;

        /**
         * Do not write files.
         *
         * @param dryRun sic
         * @return this
         */
        public Builder dryRun(boolean dryRun) {
            this.dryRun = dryRun;
            return this;
        }

        /**
         * Add extra resources not with in project path.
         *
         * @param extraResources to add
         * @return this
         */
        public Builder extraResources(List<String> extraResources) {
            this.extraResources = extraResources;
            return this;
        }

        /**
         * Add the locations configuration
         *
         * @param locations to use
         * @return this
         */
        public Builder pathLocations(PathLocations locations) {
            this.locations = locations;
            return this;
        }

        /**
         * Add a logger to the configuration.
         *
         * @param logger sic
         * @return this
         */
        public Builder logger(Logger logger) {
            this.logger = logger;
            return this;
        }

        /**
         * Produce a configured CodeStripper.
         *
         * @return the stripper
         */
        public CodeStripper build() {
            CodeStripper result = null;
            if ( logger == null ) {
                System.err.println(
                        "warning logger not configured, using default System.out." );
                logger = new Logger();
            }
            try {
                result = new CodeStripper( logger, dryRun, locations )
                        .extraResources( extraResources );
            } catch ( IOException ex ) {
                logger.error( () -> ex.getMessage() );
                throw new RuntimeException( ex );
            }
            return result;
        }

    }

}
