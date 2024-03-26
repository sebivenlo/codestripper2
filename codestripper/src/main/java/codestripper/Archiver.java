package codestripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.function.Predicate;
import mytinylogger.Logger;

/**
 * Archives for codestripper.
 *
 * The archiver maintains 3 archives: solution.zip, assignment.zip and
 * outDir/assignment which is a directory with the result of the stripper
 * actions.
 *
 * The outDir/assignment is equivalent to unpacking the assignment zip in the
 * outDir and is there to compiler validate the code. It can also be used to
 * test the stripped code.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
final class Archiver implements AutoCloseable {

    private final PathLocations locations;
    private final Logger logger;

    /**
     * Create a new archiver.
     *
     * @param log to use
     * @param locations directory and name info to use
     * @throws IOException should not occur.
     */
    public Archiver(Logger log, PathLocations locations)
            throws IOException {
        this.locations = locations;
        this.logger = log;
        this.solution = new Zipper( logger, this.locations.out()
                .resolve( "solution.zip" ) );
        assignment = new Zipper( logger, this.locations.out()
                .resolve( this.locations
                        .assignmentName() + ".zip" ) );
    }
    final Zipper solution;
    final Zipper assignment;

    public String projectName() {
        return locations.projectName();
    }

    /**
     * Archive the given lines in file in the assignment archive outDir and zip.
     *
     * @param file name of zip entry
     * @param lines to add
     */
    void addAssignmentLines(Path file, List<String> lines) throws IOException {
        Path pathInZip = pathInZip( "assignment", file );
        addLinesToZip( assignment, pathInZip, lines );
        Path targetFile = locations.expandedArchive()
                .resolve( pathInZip );
        Files.createDirectories( targetFile.getParent() );
        Files.write( targetFile, lines );
    }

    void addSolutionLines(Path file, List<String> lines) throws IOException {
        Path pathInZip = pathInZip( "solution", file );
        addLinesToZip( solution, pathInZip, lines );
    }

    Path pathInZip(String sol, Path file) {
        Path pathInZip = Path.of( sol, locations.projectName() )
                .resolve( locations.workRelative( file ) );
        return pathInZip;
    }

    void addLinesToZip(Zipper zipper, Path file, List<String> lines) throws IOException {
        zipper.add( file, lines );
    }

    /**
     * Add file to all archive types.
     *
     * @param file to add.
     */
    void addFile(Path file) {
        // find relative path from work dir to file and use that in archive
        Path x = locations.workRelative( file );
        //solution contains parent/project
        Path inZip = locations.inZip( "solution", x );
        solution.add( inZip, file );

        Path inZip2 = locations.inZip( "assignment", x );
        assignment.add( inZip2, file );
        addAssignmentFile( inZip2, file );
    }

    /**
     * Copy file to expanded archive.
     *
     * @param path in the archive
     * @param source file
     */
    void addAssignmentFile(Path destFile, Path source) {
        try {
            Path archiveFile = locations.inArchive( destFile );
            Files.createDirectories( archiveFile
                    .getParent() );
            Files.copy( locations.inWorkFile( source ), archiveFile,
                    StandardCopyOption.REPLACE_EXISTING );
        } catch ( IOException ex ) {
            logger.error( () -> "io exception on " + ex.getMessage() );
        }
    }

    /**
     * Add the listed resources to the archives. The extraResources are resolved
     * against the current working directory, which is typically the directory
     * that contains the pom.xml file.
     *
     * @param extraResources
     */
    void addExtras(List<String> extraResources) {
        if ( extraResources.isEmpty() ) {
            logger.info( () -> "no extraResources specified" );
            return;
        }
        logger.debug( () -> "Add extras" );
        for ( String extraResource : extraResources ) {
            logger.debug( () -> "considering extra resource " + extraResource );
            try {

                var toZip = locations.inWorkFile( extraResource )
                        .normalize();
                if ( Files.notExists( toZip ) ) {
                    logger.warn(
                            () -> "file resource does not exist \033[33m " + toZip
                                    .toString() + "\033[m" );
                    continue;
                }
                if ( Files.isRegularFile( toZip ) ) {
                    logger.info(
                            () -> "adding file \033[32m" + extraResource + "\033[m" );
                    addFile( toZip );
                } else if ( Files.isDirectory( toZip ) ) {
                    Files.walk( toZip, Integer.MAX_VALUE )
                            .filter( f -> locations.acceptablePath( f ) )
                            .map( f -> locations.work()
                            .relativize( f ) )
                            .peek( f -> logger.info(
                            () -> "adding file \033[32m" + f + "\033[m" ) )
                            .forEach( p -> addFile( p ) );
                } else {
                    logger.warn(
                            () -> "Not a file or dir: \033[33m" + extraResource + "\033[m" );
                }
            } catch ( IOException ex ) {
                logger.error( () -> ex.getMessage() );
            }

        }

    }

    @Override
    public void close() throws Exception {
        if ( null != solution ) {
            solution.close();
        }
        if ( null != assignment ) {
            assignment.close();
        }
    }

    /**
     * Process the non-text files in the root directory. Typically this is the
     * directory that contains the maven pom file.
     *
     * Binary files are those that are not text according to
     * CodeStripper#isText.
     *
     * @param root directory of the maven project
     * @throws IOException should not occur.
     */
    void addAssignmentFiles(Path root) throws IOException {
        Path pwd = locations.work();
        Files.walk( root, Integer.MAX_VALUE )
                .filter( f -> locations.acceptablePath( f ) )
                .filter( Predicate.not( ChippenDale::isText ) )
                .map( p -> pwd.relativize( p.toAbsolutePath() ) )
                .peek( f -> logger.debug( () -> "bin file added \033[35m" + f
                .toString() + "\033[m" ) )
                .forEach( file -> addFile( file ) );
    }

}
