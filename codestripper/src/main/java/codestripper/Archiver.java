package codestripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.function.Predicate;
import org.apache.maven.plugin.logging.Log;

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
final class Archiver implements ChippenDale, AutoCloseable {

    private final Path outDir;
    private final Log logger;

    private final String projectName;
    private final String assignmentZipName;

    /**
     * Create a new archiver
     *
     * @param log to use
     * @param outDirPath base of archives
     * @param assignmentZipName assignment zip has configurable name
     * @param projectName of the project
     * @throws IOException should not occur.
     */
    public Archiver(Log log, Path outDirPath, String assignmentZipName,
            String projectName)
            throws IOException {
        this.logger = log;
        this.outDir = outDir( outDirPath );
        this.assignmentZipName = assignmentZipName;
        this.projectName = projectName;
        this.solution = new Zipper( outDir().resolve( "solution.zip" ) );
        assignment = new Zipper( outDir().resolve( "assignment.zip" ) );
    }
    final Zipper solution;
    final Zipper assignment;

    @Override
    public String projectName() {
        return this.projectName;
    }

    /**
     * Archive the given lines in file in the assignment archive outDir and zip.
     *
     * @param file name of zip entry
     * @param lines to add
     */
    void addAssignmentLines(Path file, List<String> lines) throws IOException {
        Path pathInZip = relPathInArchive( "assignment", file );
        addLinesToZip( assignment, pathInZip, lines );
        Path targetFile = expandedArchive().resolve( pathInZip );
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

    Path expandedArchive = null;

    /**
     * Add file to all archive types.
     *
     * @param file to add.
     */
    void addFile(Path file) {
        // find relative path from pwd to file and use that in archive

        solution.add( relPathInArchive( "solution", file ), file );
        Path relPathInArchive = relPathInArchive( "assignment", file );
        assignment.add( relPathInArchive, file );
        addAssignmentFile( relPathInArchive, file );
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

                var inZip = pwd.resolve( extraResource ).normalize();
                if ( Files.notExists( inZip ) ) {
                    logger.warn( "file resource does not exist " + inZip
                            .toString() );
                    continue;
                }
                if ( Files.isRegularFile( inZip ) ) {
                    logger.info( "adding file " + inZip.toString() );
                    addFile( inZip );
                } else if ( Files.isDirectory( inZip ) ) {
                    Files.walk( inZip, Integer.MAX_VALUE )
                            .filter( f -> acceptablePath( f, outDir ) )
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

    final Path outDir(Path outDirPath) {
        Path result = null;
        try {
            Path absPath = outDirPath.toAbsolutePath();
            if ( !absPath.toFile().exists() ) {
                result = Files.createDirectories( absPath );
            }
        } catch ( IOException ex ) {
            logger.error( ex.getMessage() );
            ex.printStackTrace();
        }

        return result;
    }

    public Path outDir() {
        return outDir;
    }

    /**
     *
     * @return the location of the expanded Archive.
     */
    Path expandedArchive() throws IOException {
        if ( null == expandedArchive ) {
            this.expandedArchive = outDir().resolve( "expandedArchive" );
        }
        return expandedArchive;
    }

    /**
     * Get the directory where all project files land.
     *
     * @return the path to the project in the expandedArchive.
     */
    protected Path projectDir() throws IOException {
        return expandedArchive().resolve( "assignment" )
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
        Files.walk( root, Integer.MAX_VALUE )
                .filter( f -> acceptablePath( f, outDir ) )
                .filter( Predicate.not( ChippenDale::isText ) )
                .map( p -> pwd.relativize( p.toAbsolutePath() ) )
                .peek( f -> logger.info( "bin file added" + f.toString() ) )
                .forEach( file -> addFile( file ) );
    }

}
