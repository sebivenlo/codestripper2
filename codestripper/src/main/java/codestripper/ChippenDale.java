package codestripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

/**
 * Parent of classes in this package that should have the same view on things.
 * Name is a pun on a well known artist type. DragQueen would also be
 * appropriate.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 *
 * @param C subclass
 */
sealed interface ChippenDale permits Archiver, CodeStripper {

    /**
     * known text formats
     */
    static final Set<String> textExtensions = Set.of( "java", "sql",
            "txt", "sh", "yaml", "yml", "properties" );

    /**
     * Local path of .git.
     */
    static final Path dotgit = Path.of( ".git" );
    /**
     * The location of the 'unzipped' archive.
     */

    /**
     * SLF4J log.
     */
    final Path pwd = Path.of( System.getProperty( "user.dir" ) )
            .toAbsolutePath();
    static final Path target = pwd.resolve( "target" ).toAbsolutePath();

    /**
     * Get the project name. The project defaults to the name of the project
     * directory, i.e. the parent of pom.xml.
     *
     * @return project name
     */
    String projectName();

    default void checkPath(Path outDirPath1) throws IllegalArgumentException, IOException {
        if ( !outDirPath1.toRealPath().toFile().exists() ) {
            throw new IllegalArgumentException(
                    "Path " + outDirPath1 + "must exists" );
        }
    }

    static boolean isText(Path file) {
        String fileNameString = file.getFileName().toString();
        String[] split = fileNameString.split( "\\.", 2 );
        if ( split.length < 2 ) {
            // no extension
            return false;
        }
        String extension = split[ 1 ];
        return Archiver.textExtensions.contains( extension );
    }
    /**
     * Determine if a path is acceptable as location for resources. Used to test
     * directories and files.
     *
     * @param p path to test
     * @return true if acceptable false otherwise.
     */
    default boolean acceptablePath(Path p, Path forbidden) {
        if ( p.toString().startsWith( ".git" ) ) {
            return false;
        }
        if ( p.getFileName().toString().startsWith( ".git" ) ) {
            return false;
        }
        Path absPath = p.toAbsolutePath();
        var itr = absPath.iterator();
        // no .git in dir name
        while ( itr.hasNext() ) {
            if ( itr.next().getFileName().toString().equals( ".git" ) ) {
                return false;
            }
        }
        if ( absPath.startsWith( forbidden ) ) {
            return false;
        }
        if ( absPath.startsWith( target ) ) {
            return false;
        }
        if ( Files.isDirectory( absPath ) ) {
            return false;
        }
        if ( absPath.getFileName().toString().endsWith( "~" ) ) {
            return false;
        }
        return true;
    }

}
