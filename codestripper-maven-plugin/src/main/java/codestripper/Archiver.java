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

    public Archiver(String outDir, Log log) {
        this.outDir = Path.of( System.getProperty( "user.dir" ) ).resolve(
                outDir );
        this.log = log;
        solution = new Zipper( this.outDir.resolve( "solution.zip" ) );
        assignment = new Zipper( this.outDir.resolve( "assignment.zip" ) );
    }
    final Zipper solution;
    final Zipper assignment;

    ;

    /**
     * Archive the given lines in file in the assignment archive outDir and zip.
     *
     * @param file
     * @param lines
     */
    void addAssignmentLines(Path file, List<String> lines) throws IOException {
        addLinesToZip( assignment, file, lines );
        // writeLinesToFile Path outdir+assignment+file
        Path targetFile = outDir.resolve( "assignment/" + file.toString() );
        Files.createDirectories( targetFile.getParent() );
        Files.write( targetFile, lines );

    }

    void addSolutionLines(Path file, List<String> lines) throws IOException {
        Path path = Path.of( "solution", file.toString() );
        addLinesToZip( solution, path, lines );
    }

    void addLinesToZip(Zipper zipper, Path file, List<String> lines) throws IOException {
        zipper.add( file, lines );
    }

    final Path target = Path.of( "target" );
    final Path dotgit = Path.of( ".git" );

    /**
     * Process the files in the root directory. Typically this is the directory
     * that contains the maven pom file.
     *
     * Binary files are those that are not text according to
     * CodeStripper#isText.
     *
     * @param root directory of the maven project
     * @throws IOException should not occur.
     */
    void addBinaryFiles(Path root) throws IOException {
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
        System.out.println( "file = " + file.toString() );
        Path inzip = Path.of( "solution", file.toString() );
        System.out.println( "inzip = " + inzip );
        solution.add( inzip, file );
//        assignment.add( Path.of( "assignment", file.toString() ), file );
        addConcreteFile( file );
    }

    void addConcreteFile(Path file) {
        Path targetFile = outDir.resolve( "assignment/" + file.toString() );
        try {
            Files.createDirectories( targetFile.getParent() );
            Files.copy( file, targetFile, StandardCopyOption.REPLACE_EXISTING );
        } catch ( IOException ex ) {
            log.warn( ex.getMessage() );
        }
    }

    void addExtras(Path root, List<String> extraResources) {
        log.info( "Add extras" );
        if ( extraResources.isEmpty() ) {
            log.info( "no resources found" );
            return;
        }
        // no deeper that parent, assuming this maven project lives in solution or similar.
        Path parent = root.getParent();

        for ( String extraResource : extraResources ) {
            log.info( "considering extra resource " + extraResource );
            try {
                var rPath = Path.of( extraResource );
                if ( Files.notExists( rPath ) ) {
                    log.warn( "file resource does not exist " + extraResource );
                    continue;
                }
                // specify resource in unix style
                var resourceReal = rPath.toRealPath();
                var resourceInzip = parent.relativize( resourceReal );
                if ( Files.isRegularFile( rPath ) ) {
                    addFileResource( solution, assignment, resourceInzip, rPath );
                } else if ( Files.isDirectory( rPath ) ) {
                    Files.walk( rPath, Integer.MAX_VALUE )
                            .filter( f -> !Files.isDirectory( f ) )
                            .peek( r -> log.info( "files" + r.toString() ) )
                            .forEach(
                                    p -> addFileResource( solution, assignment,
                                            parent.relativize( p ), p )
                            );
                } else {
                    log.warn( "Not a file or dir" );
                }
//                addFile( resourceInzip, solution, assignment );
            } catch ( IOException ex ) {
                Logger.getLogger( CodeStripper.class.getName() )
                        .log( Level.SEVERE, null, ex );
            }

        }

    }

    /**
     * Add a resource file to the destination solution, assignment and outDir.
     *
     * @param solution zipper
     * @param assignment zipper
     * @param resourceInzip name inside zip files
     * @param rPath path the actual resource
     */
    private void addFileResource(Zipper solution, Zipper assignment,
            Path resourceInzip, Path rPath) {
        solution.add( resourceInzip, rPath );
        assignment.add( resourceInzip, rPath );
        Path targetFile = outDir.resolve( resourceInzip );
        try {
            Files.createDirectories( targetFile.getParent() );
            Files.copy( rPath, targetFile,
                    StandardCopyOption.REPLACE_EXISTING );

        } catch ( IOException ex ) {
            Logger.getLogger( CodeStripper.class.getName() )
                    .log( Level.SEVERE, null, ex );
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
