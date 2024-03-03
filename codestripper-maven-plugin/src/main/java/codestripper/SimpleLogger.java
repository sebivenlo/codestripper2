/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codestripper;

import java.util.function.Supplier;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public interface SimpleLogger {

    LoggerVerbosity verbosity();
    void log(String msg);

    default void low(Supplier<String> msg) {
        if ( verbosity().compareTo( LoggerVerbosity.LOW ) == 0 ) {
            log( msg.get() );
        }
    }

    default void intermediate(Supplier<String> msg) {
        if ( verbosity().compareTo( LoggerVerbosity.INTERMEDIATE ) <= 0 ) {
            log( msg.get() );
        }

    }

    default void high(Supplier<String> msg) {
        if ( verbosity().compareTo( LoggerVerbosity.HIGH ) <= 0 ) {
            log( msg.get() );
        }
    }
}
