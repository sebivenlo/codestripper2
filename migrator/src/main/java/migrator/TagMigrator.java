package migrator;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Migrates the code stripper v1 tags to the new code stripper tags.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class TagMigrator {

    /**
     * Entry of program
     *
     * @param args not used
     * @throws IOException should not occur.
     */
    public static void main(String[] args) throws IOException {
        Path root = Path.of( System.getProperty( "user.dir" ) );
        new TagMigrator().migrate( root );
    }

    /**
     * Regexes are costly to compose.
     */
    public static final Pattern myPreciousPattern = Pattern.compile(
            "(?<indent>\\s*)" // optinal identation
            + "//(?<startEnd>(Start|End))" // required Start or End
            + " Solution" // required space+word
            + "(::replaceWith::(?<payLoad>.*))?" // optional payload
    );

    /**
     * Do the work.
     */
    void migrate(Path root) throws IOException {
        FileSystem def = FileSystems.getDefault();
        PathMatcher pm = def.getPathMatcher( "glob:**/*.java" );
        Files.walk( root, Integer.MAX_VALUE )
                .filter( p -> pm.matches( p ) )
                .forEach( file -> {
                    try {
                        process( file );
                    } catch ( IOException ex ) {
                        java.util.logging.Logger.getLogger( TagMigrator.class
                                .getName() ).severe( ex.getMessage() );
                        throw new UncheckedIOException( ex );
                    }
                } );
    }
    private static String lineSep = System.getProperty( "line.separator" );

    private void process(Path file) throws IOException {

        // read all lines from file, pass them through a test and replace if needed
        var migratedlines
                =        Files.lines( file )
                        .map( s -> s ).toList();
        String backup = file.getFileName().toString() + "-bak";
        Files.move( file, file.getParent().resolve( backup ), REPLACE_EXISTING );
        Files.write( file, migratedlines );
    }

    /**
     * Mapper for lines, used in stream.
     *
     * @param in line
     * @return possibly migrated line
     */
    String migrateLine(String in) {
        Matcher m = myPreciousPattern.matcher( in );
        if ( !m.matches() ) {
            return in;
        }
        String result = m.group( "indent" ) + "//cs:remove";
        if ( null != m.group( "startEnd" ) ) {
            String startEnd = m.group( "startEnd" ).toLowerCase();
            result += ":" + startEnd;
        }
        if ( null != m.group( "payLoad" ) ) {
            String payLoad = m.group( "payLoad" );
            result += ":" + payLoad;
        }

        return result;
    }

}
