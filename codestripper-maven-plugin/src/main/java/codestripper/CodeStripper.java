package codestripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import streamprocessor.ProcessorFactory;

/**
 * The work horse of the code stripper.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class CodeStripper {

    /**
     * Do the work starting at the root.
     *
     * @param root of the stripping action.
     *
     * @throws IOException hop to die
     */
    public final void strip(String root) throws IOException {
        try ( Zipper solution = new Zipper( "target/solution.zip" ); //
                  Zipper assignment = new Zipper( "target/assignment.zip" ); ) {
            processTextFiles( root, solution, assignment );
            processBinaryFiles( root, solution, assignment );
        } catch ( Exception ex ) {
            Logger.getLogger( CodeStripper.class.getName() )
                    .log( Level.SEVERE, null, ex );
        }
        // save file names for later zipping.
    }

    Path target = Path.of( "target" );
    Path dotgit = Path.of( ".git" );

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
    void processTextFiles(String root,
            final Zipper solution, final Zipper assignment) throws IOException {
        Files.walk( Path.of( root ), Integer.MAX_VALUE )
                .filter( f -> !Files.isDirectory( f ) )
                //                .filter( f -> !f.startsWith( out ) )
                .filter( f -> !f.startsWith( target ) )
                .filter( f -> !f.startsWith( dotgit ) )
                .filter( this::isText )
                .filter( f -> !f.getFileName().toString().endsWith( "~" ) )
                .sorted()
                .forEach( file -> process( file, solution, assignment ) );
    }

    /**
     * Process the files in the root directory. Typically this is the directory
     * that contains the maven pom file.
     *
     * Binary files are those that are not text according to
     * CodeStripper#isText.
     *
     * @param root directory of the maven project
     * @param target to not visit
     * @param dotgit ignore
     * @param solution zip container for solution files
     * @param assignment zip container for assignment files
     * @throws IOException should not occur.
     */
    void processBinaryFiles(String root,
            final Zipper solution, final Zipper assignment) throws IOException {
        Files.walk( Path.of( root ), Integer.MAX_VALUE )
                .filter( f -> !Files.isDirectory( f ) )
                //                .filter( f -> !f.startsWith( out ) )
                .filter( f -> !f.startsWith( target ) )
                .filter( f -> !f.startsWith( dotgit ) )
                .filter( f -> !f.getFileName().toString().endsWith( "~" ) )
                .filter( f -> !isText( f ) )
                .sorted()
                .forEach( file -> addFile( file, solution, assignment ) );
    }

    /**
     * Adds file to solution, assignment and stripper out dir.
     *
     * @param file to add
     * @param solution zip container
     * @param assignment zip container
     */
    void addFile(Path file, Zipper solution, Zipper assignment) {
        // prepend solution in solution
        solution.add( Path.of( "solution", file.toString() ), file );
        assignment.add( Path.of( "assignment", file.toString() ), file );
        // put file in outDir too, prepending
        Path targetFile = outDir.resolve( file );
        try {
            Files.createDirectories( targetFile.getParent() );
            Files.copy( file, targetFile ,StandardCopyOption.REPLACE_EXISTING);

        } catch ( IOException ex ) {
            Logger.getLogger( CodeStripper.class.getName() )
                    .log( Level.SEVERE, null, ex );
        }
    }
    private final Set<String> textExtensions = Set.of( "java", "sql", "txt",
            "sh",
            "yaml", "yml" );

    boolean isText(Path file) {
        String fileNameString = file.getFileName().toString();
        String[] split = fileNameString.split( "\\.", 2 );
        if ( split.length < 2 ) {
            // no extension
            return false;
        }
        String extension = split[ 1 ];
        return textExtensions.contains( extension );
    }

    private Path outDir = Path.of( "target/stripper-out" );

    private void process(Path javaFile, Zipper solution, Zipper assignment) {
        Path targetFile = outDir.resolve( javaFile );
        var factory = new ProcessorFactory( javaFile );
        try {
            var lines = Files.lines( javaFile ).toList();
            // unprocessed files go to solution
            solution.add( Path.of( "solution", javaFile.toString() ), lines );
            List<String> result = lines.stream()
                    .map( factory::apply )
                    .flatMap( x -> x ) // flatten the result
                    .toList();

            // add to assigmnet after processing
            assignment
                    .add( Path.of( "assignment", javaFile.toString() ), result );
            if ( !result.isEmpty() ) {
                Files.createDirectories( targetFile.getParent() );
                Files.write( targetFile, result );
            }
        } catch ( IOException ex ) {
            LOG.severe( ex.getMessage() );
        }

        if ( factory.hasDanglingTag() ) {
            System.out.println(
                    "file " + javaFile.toString() + " has dangling tag, started at " + factory
                    .danglingTag() );
        }
    }

    private static final Logger LOG = Logger.getLogger( CodeStripper.class
            .getName() );

    /**
     * Who doesn't need me.
     *
     * @param args to ignore
     * @throws IOException hope to die
     */
    public static void main(String... args) throws IOException {
        new CodeStripper().strip( "" );
    }

    /**
     * No specialties needed.
     */
    public CodeStripper() {
    }

}
