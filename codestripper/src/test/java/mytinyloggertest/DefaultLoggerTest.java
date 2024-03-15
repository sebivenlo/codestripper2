package mytinyloggertest;

import loggerwrapper.Logger;
import mytinylogger.DefaultLogger;
import static loggerwrapper.LoggerLevel.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class DefaultLoggerTest implements AppendAndClear {

     public DefaultLoggerTest() {
    }

    StringBuilder buffer = new StringBuilder();
    @Test
    public void testLevel() {
        Logger logger = new DefaultLogger();
        assertThat( logger.level() ).isEqualTo( MUTE );

    }

    //@Disabled("think TDD")
    @Test @DisplayName( "can I change the level" )
    public void testSetLevel() {

        Logger logger = new DefaultLogger().level( INFO );
        assertThat( logger.level() ).isEqualTo( INFO );

//        fail( "method SetLevel reached end. You know what to do." );
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "compare levels" )
    public void testCompareLevels() {
        assertThat( ERROR.greaterEqual( MUTE ) ).isTrue();
//        fail( "method CompareLevels reached end. You know what to do." );
    }

    @Override
    public void appendText(String toAppend) {
        buffer.append( toAppend );
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "that that logger prints" )
    public void testLogs() {
        ConsoleOutput consoleOutput = new ConsoleOutput( this );
        Logger logger = new DefaultLogger( consoleOutput.asPrintStream() )
                .level( INFO );
        logger.info( () -> "Hello" );
        assertThat( consoleOutput.toString() ).isNotEmpty();
//        fail( "method Logs reached end. You know what to do." );
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "lower is silence" )
    public void testLogsMUTE() {
        ConsoleOutput consoleOutput = new ConsoleOutput( this );
        Logger logger = new DefaultLogger( consoleOutput.asPrintStream() )
                .level( MUTE );
        logger.info( () -> "Hello" );
        assertThat( consoleOutput.toString() ).isEmpty();
//        fail( "method LogsMUTE reached end. You know what to do." );
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "warning shouts" )
    public void testLogsWARN() {
        ConsoleOutput consoleOutput = new ConsoleOutput( this );
        Logger logger = new DefaultLogger( consoleOutput.asPrintStream() )
                .level( INFO );
        logger.warn( () -> "Hello" );
        assertThat( consoleOutput.toString() ).isNotEmpty();
//        fail( "method LogsMUTE reached end. You know what to do." );
    }
    //@Disabled("think TDD")
    @Test @DisplayName( "warning yells" )
    public void testLogsError() {
        ConsoleOutput consoleOutput = new ConsoleOutput( this );
        Logger logger = new DefaultLogger( consoleOutput.asPrintStream() )
                .level( INFO );
        logger.error( () -> "Hello" );
        assertThat( buffer.toString() ).contains( "Hello" );
//        fail( "method LogsMUTE reached end. You know what to do." );
    }
}