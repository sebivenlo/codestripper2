package mytinyloggertest;

import mytinylogger.Logger;
import static mytinylogger.LoggerLevel.*;
import mytinylogger.LoggerLevel;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.CsvSource;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class LoggerTest implements AppendAndClear {

    public LoggerTest() {
    }

    StringBuilder buffer = new StringBuilder();

    @Test
    public void testLevel() {
        Logger logger = new Logger();
        assertThat( logger.level() )
                .isEqualTo( MUTE );

    }

    //@Disabled("think TDD")
    @Test @DisplayName( "can I change the level" )
    public void testSetLevel() {

        Logger logger = new Logger().level( INFO );
        assertThat( logger.level() )
                .isEqualTo( INFO );

//        fail( "method SetLevel reached end. You know what to do." );
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "compare levels" )
    public void testCompareLevels() {
        assertThat( MUTE.greaterEqual( ERROR ) )
                .isTrue();
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
        Logger logger = new Logger( consoleOutput.asPrintStream() )
                .level( INFO );
        logger.info( () -> "Hello" );
        assertThat( consoleOutput.toString() )
                .isNotEmpty();
//        fail( "method Logs reached end. You know what to do." );
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "lower is silence" )
    public void testLogsMUTE() {
        ConsoleOutput consoleOutput = new ConsoleOutput( this );
        Logger logger = new Logger( consoleOutput.asPrintStream() )
                .level( MUTE );
        logger.info( () -> "Hello" );
        assertThat( consoleOutput.toString() )
                .isEmpty();
//        fail( "method LogsMUTE reached end. You know what to do." );
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "warning shouts" )
    public void testLogsWARN() {
        ConsoleOutput consoleOutput = new ConsoleOutput( this );
        Logger logger = new Logger( consoleOutput.asPrintStream() )
                .level( INFO );
        logger.warn( () -> "Hello" );
        assertThat( consoleOutput.toString() )
                .isNotEmpty();
//        fail( "method LogsMUTE reached end. You know what to do." );
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "warning yells" )
    public void testLogsError() {
        ConsoleOutput consoleOutput = new ConsoleOutput( this );
        Logger logger = new Logger( consoleOutput.asPrintStream() )
                .level( INFO );
        logger.error( () -> "Hello" );
        assertThat( buffer.toString() )
                .contains( "Hello" );
//        fail( "method LogsMUTE reached end. You know what to do." );
    }

    //@Disabled("think TDD")
    @DisplayName( "check level usage" )
    @ParameterizedTest
    @CsvSource( {
        // level, msg, expected
        "INFO,ERROR,Hello,Hello",
        "INFO,WARN,Hello,Hello",
        "INFO,INFO,Hello,Hello", //
        "INFO,DEBUG,Hello,", //
        "INFO,FINE,Hello,", //
        "DEBUG,ERROR,Hello,Hello",
        "DEBUG,WARN,Hello,Hello",
        "DEBUG,INFO,Hello,Hello", //
        "DEBUG,DEBUG,Hello,Hello", //
        "DEBUG,FINE,Hello,", //
        "FINE,ERROR,Hello,Hello",
        "FINE,WARN,Hello,Hello",
        "FINE,INFO,Hello,Hello", //
        "FINE,DEBUG,Hello,Hello", //
        "FINE,FINE,Hello,Hello", //
    } )
    public void testEffectiveLevel(LoggerLevel current, LoggerLevel level,
            String msg, String output) {
        ConsoleOutput consoleOutput = new ConsoleOutput( this );
        Logger logger = new Logger( consoleOutput.asPrintStream() ).level(
                current );
        switch ( level ) {
            case ERROR -> logger.error( () -> msg );
            case WARN -> logger.warn( () -> msg );
            case INFO -> logger.info( () -> msg );
            case DEBUG -> logger.debug( () -> msg );
            case FINE -> logger.fine( () -> msg );
        }
        logger.logAtLevel( level, () -> msg );
        if ( null == output || output.isEmpty() ) {
            assertThat( buffer.toString() )
                    .isEmpty();
        } else {
            assertThat( buffer.toString() )
                    .contains( output );

        }
//        fail( "method EffectiveLevel reached end. You know what to do." );
    }
}
