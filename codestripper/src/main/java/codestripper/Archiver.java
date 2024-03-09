package codestripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.function.Predicate;
import org.slf4j.Logger;

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
final class Archiver extends ChippenDale implements AutoCloseable {

    public Archiver(Path outDir, Logger log) throws IOException {
        super( log, outDir );
        solution = new Zipper( outDir().resolve( "solution.zip" ) );
        assignment = new Zipper( outDir().resolve( "assignment.zip" ) );
    }
    final Zipper solution;
    final Zipper assignment;

    /**
     * Archive the given lines in file in the assignment archive outDir and zip.
     *
     * @param file name of zip entry
     * @param lines to add
     */
    void addAssignmentLines(Path file, List<String> lines) throws IOException {
        Path pathInZip = relPathInArchive( "assignment", file );
        addLinesToZip( assignment, pathInZip, lines );
        Path targetFile = this.expandedArchive.resolve( pathInZip );
        Files.createDirectories( targetFile.getParent() );
        Files.write( targetFile, lines );
    }

    void addSolutionLines(Path file, List<String> lines) throws IOException {
        Path pathInZip = relPathInArchive( "solution", file );
        addLinesToZip( solution, pathInZip, lines );
    }

    void addLinesToZip(Zipper zipper, Path file, List<String> lines) throws IOException {
        zipper.add( file, lines );
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
        Files.walk( root, Integer.MAX_VALUE )
                .filter( this::acceptablePath )
                .filter( Predicate.not( this::isText ) )
                .sorted()
                .forEach( file -> addFile( file ) );
    }

    /**
     * Add file to all archive types.
     *
     * @param file to add.
     */
    void addFile(Path file) {
        // find relative path from pwd to file and use that in archive

        Path inZip = Path.of( projectName() ).resolve( file );
        solution.add( inZip, file );
        assignment.add( inZip, file );
        Path inAssignment = relPathInArchive( "assignment", file );
        addAssignmentFile( inAssignment, file );
    }

    Path relPathInArchive(String archive, Path file) {
        Path relPath = pwd.relativize( pwd.resolve( file ).normalize() );
        var relPathResult = Path.of( archive ).resolve( projectName() ).resolve(
                relPath )
                .normalize();
        if ( relPathResult.normalize().isAbsolute() ) {
            throw new RuntimeException( "illegal path constructed" );
        }
        return relPathResult;
    }

    void addAssignmentFile(Path inArchive, Path source) {
        try {
            Path archiveFile = expandedArchive().resolve( inArchive );
            Files.createDirectories( archiveFile.getParent() );
            Files.copy( source, archiveFile,
                    StandardCopyOption.REPLACE_EXISTING );
        } catch ( IOException ex ) {
            logger.warn( "io exception on " + ex.getMessage() );
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
        logger.info( "Add extras" );
        if ( extraResources.isEmpty() ) {
            logger.info( "no resources found" );
            return;
        }
        for ( String extraResource : extraResources ) {
            logger.info( "considering extra resource " + extraResource );
            try {

                var rPath = pwd.resolve( extraResource ).normalize();
                if ( Files.notExists( rPath ) ) {
                    logger.warn( "file resource does not exist " + rPath
                            .toString() );
                    continue;
                }
                if ( Files.isRegularFile( rPath ) ) {
                    var resourceInzip = pwd.relativize( rPath.toAbsolutePath() );
                    logger.info( "adding file " + resourceInzip.toString() );
                    addFile( resourceInzip );
                } else if ( Files.isDirectory( rPath ) ) {
                    Files.walk( rPath, Integer.MAX_VALUE )
                            .filter( this::acceptablePath )
                            .map( f -> pwd.relativize( f.toAbsolutePath() ) )
                            .forEach(
                                    p -> addFile( p )
                            );
                } else {
                    logger.warn( "Not a file or dir" );
                }
            } catch ( IOException ex ) {
                logger.error( ex.getMessage() );
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

}
