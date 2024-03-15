package codestripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import loggerwrapper.Logger;
import loggerwrapper.LoggerLevel;

/**
 * Migrates the code stripper v1 tags to the new code stripper tags.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class TagMigrator {

    PathLocations locations;
    private final Logger logger;
    LoggerLevel logLevel = LoggerLevel.INFO;

    public TagMigrator(Logger logger, PathLocations locations) {
        this.logger = logger;
        this.locations = locations;

    }

    Pattern myPreciousPattern = Pattern.compile(
            "(?<indent>\\s+)//(?<startEnd>(Start|End)) Solution(::replaceWith::(?<payLoad>).*)$" );

    void migrate() throws IOException {
        Files.walk( locations.work(), Integer.MAX_VALUE )
                .filter( f -> locations.acceptablePath( f ) )
                .filter( ChippenDale::isText )
                .forEach( file -> {
                    try {
                        process( file );
                    } catch ( IOException ex ) {
                        java.util.logging.Logger.getLogger( TagMigrator.class
                                .getName() )
                                .log( Level.SEVERE, null, ex );
                    }
                } );
    }

    private static String lineSep = System.getProperty( "line.separator" );

    private void process(Path file) throws IOException {

        // read all lines from file, pass them through a test and replace if needed
        var migratedlines = Files.lines( file )
                .map( s -> s ).toList();
    }

    public String migrateLine(String in) {
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
