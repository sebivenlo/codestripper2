/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codestripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
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
class Archiver implements AutoCloseable {

    final Log log;
    final Path outDir;
    private final Path expandedArchive;
    final Path pwd = Path.of( System.getProperty( "user.dir" ) )
            .toAbsolutePath();

    public Archiver(String outDirName, Log log) {
        this.outDir = pwd.resolve( outDirName );
        this.log = log;
        solution = new Zipper( outDir.resolve( "solution.zip" ) );
        assignment = new Zipper( outDir.resolve( "assignment.zip" ) );
        this.expandedArchive = outDir.resolve( "assignment" );
    }
    final Zipper solution;
    final Zipper assignment;

    /**
     *
     * @return
     */
    public Path expandedArchive() {
        return expandedArchive;
    }

    /**
     * Archive the given lines in file in the assignment archive outDir and zip.
     *
     * @param file
     * @param lines
     */
    void addAssignmentLines(Path file, List<String> lines) throws IOException {
        Path path = Path.of( "assessment" ).resolve( file );
        addLinesToZip( assignment, path, lines );
        Path targetFile = this.expandedArchive.resolve( path );
        Files.createDirectories( targetFile.getParent() );
        Files.write( targetFile, lines );
    }

    void addSolutionLines(Path file, List<String> lines) throws IOException {
        Path path = Path.of( "solution" ).resolve( file );
        addLinesToZip( solution, path, lines );
    }

    void addLinesToZip(Zipper zipper, Path file, List<String> lines) throws IOException {
        zipper.add( file, lines );
    }

    final Path target = Path.of( "target" );
    final Path dotgit = Path.of( ".git" );

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
                .filter( f -> !Files.isDirectory( f ) )
                //                .filter( f -> !f.startsWith( out ) )
                .filter( f -> !f.startsWith( target ) )
                .filter( f -> !f.startsWith( dotgit ) )
                .filter( f -> !f.getFileName().toString().endsWith( "~" ) )
                .filter( Predicate.not( Archiver::isText ) )
                .sorted()
                .forEach( file -> addFile( file ) );
    }

    private static final Set<String> textExtensions = Set.of( "java", "sql",
            "txt",
            "sh",
            "yaml", "yml" );

    static boolean isText(Path file) {
        String fileNameString = file.getFileName().toString();
        String[] split = fileNameString.split( "\\.", 2 );
        if ( split.length < 2 ) {
            // no extension
            return false;
        }
        String extension = split[ 1 ];
        return textExtensions.contains( extension );
    }

    /**
     * Add file to all archive types.
     *
     * @param file to add.
     */
    void addFile(Path file) {
        // find relative path from pwd to file and use that in archive

        Path insolution = pathInArchive( "solution", file );
        solution.add( insolution, file );
        Path inAssignment = pathInArchive( "assignment", file );
        assignment.add( inAssignment, file );
        addAssignmentFile( file, file );
    }

    Path pathInArchive(String archive, Path file) {
        Path relPath = pwd.relativize( file.toAbsolutePath().normalize() );
        return Path.of( archive ).resolve( archive ).resolve( relPath )
                .normalize();
    }

    void addAssignmentFile(Path inArchive, Path source) {
        Path targetFile = expandedArchive.resolve( "assignment" ).resolve(
                inArchive ).normalize();
        try {
            Files.createDirectories( targetFile.getParent() );
            Files.copy( source, targetFile,
                    StandardCopyOption.REPLACE_EXISTING );
        } catch ( IOException ex ) {
            log.warn( "io exception on " + ex.getMessage() );
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
        log.info( "Add extras" );
        if ( extraResources.isEmpty() ) {
            log.info( "no resources found" );
            return;
        }
        for ( String extraResource : extraResources ) {
            log.info( "considering extra resource " + extraResource );
            try {

                var rPath = pwd.resolve( extraResource ).normalize();
                if ( Files.notExists( rPath ) ) {
                    log.warn( "file resource does not exist " + rPath
                            .toString() );
                    continue;
                }
                var resourceInzip = pwd.relativize( rPath.toAbsolutePath() );
                if ( Files.isRegularFile( rPath ) ) {
                    log.info( "adding file " + resourceInzip.toString() );
                    addAssignmentFile( resourceInzip, rPath );
                } else if ( Files.isDirectory( rPath ) ) {
                    Files.walk( rPath, Integer.MAX_VALUE )
                            .filter( Predicate.not( this::isOutDir ) )
                            .filter( f -> !Files.isDirectory( f ) )
                            .peek( r -> log.info( "adding file" + r.toString() ) )
                            .map( f -> f.toAbsolutePath() )
                            .forEach(
                                    p -> addAssignmentFile( pwd.relativize( p ),
                                            p )
                            );
                } else {
                    log.warn( "Not a file or dir" );
                }
            } catch ( IOException ex ) {
                Logger.getLogger( CodeStripper.class.getName() )
                        .log( Level.SEVERE, null, ex );
            }

        }

    }

    boolean isOutDir(Path p) {
        return outDir.toAbsolutePath().equals( p.toAbsolutePath() );
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
