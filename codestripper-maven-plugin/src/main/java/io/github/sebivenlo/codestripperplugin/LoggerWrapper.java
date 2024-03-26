/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.sebivenlo.codestripperplugin;

import static mytinylogger.LoggerLevel.*;
import java.util.function.Supplier;
import loggerwrapper.Logger;
import mytinylogger.LoggerLevel;
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
public class LoggerWrapper implements Logger {

    final Log logger;
    private LoggerLevel level;

    /**
     * Create an instance with given log to be wrapped and level.
     *
     * @param log to wrap
     * @param level to use
     */
    public LoggerWrapper(Log log, LoggerLevel level) {
        this.logger = log;
        this.level = level;
    }

    @Override
    public void fine(Supplier<String> msg) {
        if ( level.greaterEqual( FINE ) ) {
            logger.info( msg.get() );
        }
    }

    @Override
    public void info(Supplier<String> msg) {
        if ( level.greaterEqual( INFO ) ) {
            logger.info( msg.get() );
        }
    }

    @Override
    public void warn(Supplier<String> msg) {
        logger.warn( msg.get() );
    }

    @Override
    public void error(Supplier<String> msg) {
        logger.error( msg.get() );
    }

    @Override
    public void debug(Supplier<String> msg) {
        if ( level.greaterEqual( DEBUG ) ) {
            logger.info( msg.get() );
        }
    }

    @Override
    public Logger level(LoggerLevel level) {
        this.level = level;
        return this;
    }

    @Override
    public LoggerLevel level() {
        return level;
    }

}
