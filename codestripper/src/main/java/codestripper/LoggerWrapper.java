/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codestripper;

import static codestripper.LoggerLevel.DEBUG;
import static codestripper.LoggerLevel.FINE;
import java.util.function.Supplier;
import org.apache.maven.plugin.logging.Log;

/**
 * Have control over own logger.
 *
 * This logger logs to the wrapped logger.info, unless the level such as warning
 * requires a higher level. Example this#warn uses the inferior logger.warn.
 *
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class LoggerWrapper {

    final Log logger;
    final LoggerLevel level;

    public LoggerWrapper(Log log, LoggerLevel level) {
        this.logger = log;
        this.level = level;
    }

    public void fine(Supplier<String> msg) {
        if ( this.level.compareTo( FINE ) >= 0 ) {
            logger.info( msg.get() );
        }
    }
    public void info(Supplier<String> msg) {
        if ( this.level.compareTo( FINE ) >= 0 ) {
            logger.info( msg.get() );
        }
    }

    public void warn(Supplier<String> msg) {
        logger.warn( msg.get() );
    }

    public void error(Supplier<String> msg) {
        logger.error( msg.get() );
    }

    public void debug(Supplier<String> msg) {
        if ( this.level.compareTo( DEBUG ) >= 0 ) {
            logger.info( msg.get() );
        }
    }
}
