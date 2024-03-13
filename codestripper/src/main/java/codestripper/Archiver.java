package codestripper;

import loggerwrapper.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.function.Predicate;

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
        this.solution = new Zipper( this.locations.out()
                .resolve( "solution.zip" ) );
        assignment = new Zipper( this.locations.out().resolve( this.locations
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
        Path targetFile = locations.expandedArchive().resolve( pathInZip );
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
//        //solution contains parent/project
        Path inZip = Path.of( "solution", locations.projectName() ).resolve( x )
                .normalize();
        solution.add( inZip, file );

        inZip = Path.of( "assignment", locations.projectName() ).resolve( x )
                .normalize();
        assignment.add( inZip, file );
        addAssignmentFile( inZip, file );
    }

    /**
     * Copy file to expanded archive.
     *
     * @param path in the archive
     * @param source file
     */
    void addAssignmentFile(Path inArchive, Path source) {
        try {
            Path archiveFile = locations.expandedArchive().resolve( inArchive );
            Files.createDirectories( archiveFile
                    .getParent() );
            Files.copy( locations.work().resolve( source ), archiveFile,
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
        logger.info( () -> "Add extras" );
        if ( extraResources.isEmpty() ) {
            logger.info( () -> "no resources found" );
            return;
        }
        for ( String extraResource : extraResources ) {
            logger.info( () -> "considering extra resource " + extraResource );
            try {

                var inZip = locations.work().resolve( extraResource ).normalize();
                if ( Files.notExists( inZip ) ) {
                    logger.warn( () -> "file resource does not exist " + inZip
                            .toString() );
                    continue;
                }
                if ( Files.isRegularFile( inZip ) ) {
                    logger.info( () -> "adding file " + inZip.toString() );
                    addFile( inZip );
                } else if ( Files.isDirectory( inZip ) ) {
                    Files.walk( inZip, Integer.MAX_VALUE )
                            .filter( f -> locations.acceptablePath( f ) )
                            .map( f -> locations.work().relativize( f ) )
                            .forEach( p -> addFile( p ) );
                } else {
                    logger.warn( () -> "Not a file or dir: " + extraResource );
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

    final Path outDir(Path outDirPath) {
        Path result = null;
        try {
            Path absPath = outDirPath.toAbsolutePath();
            if ( !absPath.toFile().exists() ) {
                result = Files.createDirectories( absPath );
            }
        } catch ( IOException ex ) {
            logger.error( () -> ex.getMessage() );
        }

        return result;
    }

    /**
     * Get the directory where all project files land.
     *
     * @return the path to the project in the expandedArchive.
     */
    protected Path projectDir() throws IOException {
        return locations.expandedArchive().resolve( "assignment" )
                .resolve( projectName() );
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
                .peek( f -> logger.info( () -> "bin file added" + f.toString() ) )
                .forEach( file -> addFile( file ) );
    }

}
