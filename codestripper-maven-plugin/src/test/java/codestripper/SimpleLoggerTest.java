/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package codestripper;

import static codestripper.LoggerVerbosity.LOW;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class SimpleLoggerTest implements SimpleLogger {

    LoggerVerbosity testVerbosity = LOW;
    String received = "";

    @DisplayName( "Test logging at low level, always output?" )
    @ParameterizedTest
    @CsvSource( {
        "LOW,Low level,Low level",
        "MUTE,Low level,''",
        "INTERMEDIATE,Intermediate level,'Intermediate level'",//
        "HIGH,High level,'High level'",//
    } )
    public void testLow(String verbosityLevel, String msg,
            String expected) {
        this.testVerbosity = LoggerVerbosity.valueOf( verbosityLevel );
        this.low( () -> msg );
        assertThat( received ).isEqualTo( expected );

    }

    @DisplayName( "Test logging at intermediate level, always output?" )
    @ParameterizedTest
    @CsvSource( {
        "LOW,Low level,'Low level'",
        "MUTE,Mute level,''",
        "INTERMEDIATE,Intermediate level,'Intermediate level'",//
        "HIGH,High level,'High level'",//
    } )
    public void testInter(String verbosityLevel, String msg,
            String expected) {
        this.testVerbosity = LoggerVerbosity.valueOf( verbosityLevel );
        this.intermediate( () -> msg );
        assertThat( received ).isEqualTo( expected );

    }

    @Override
    public LoggerVerbosity verbosity() {
        return testVerbosity;
    }

    @Override
    public void log(String msg) {
        this.received = msg;
    }

}
