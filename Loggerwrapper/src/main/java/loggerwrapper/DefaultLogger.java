/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loggerwrapper;

import java.io.PrintStream;
import java.util.function.Supplier;
import static loggerwrapper.LoggerLevel.*;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class DefaultLogger implements Logger {

    LoggerLevel level = LoggerLevel.MUTE;

    PrintStream out;

    public DefaultLogger(PrintStream out) {
        this.out = out;
    }

    public DefaultLogger() {
        this( System.out );
    }

    static String errorP = "[\033[31mERROR\033[m] ";
    static String infoP = "[\033[34mERROR\033[m] ";
    static String warnP = "[\033[33mERROR\033[m] ";
    static String debugP = "[\033[41;30mERROR\033[m] ";
    static String fineP = "[\033[42;30mERROR\033[m] ";

    @Override
    public void debug(Supplier<String> msg) {
        if ( level.greaterEqual( DEBUG ) ) {
            out.println( debugP + msg.get() );
        }
    }

    @Override
    public void error(Supplier<String> msg) {
        if ( level.greaterEqual( ERROR ) ) {
            out.println( errorP + msg.get() );
        }
    }

    @Override
    public void fine(Supplier<String> msg) {
        if ( level.greaterEqual( FINE ) ) {
            out.println( fineP + msg.get() );
        }
    }

    @Override
    public void info(Supplier<String> msg) {
        if ( level.greaterEqual( INFO ) ) {
            out.println( infoP + msg.get() );
        }
    }

    @Override
    public void warn(Supplier<String> msg) {
        if ( level.greaterEqual( WARN ) ) {
            out.println( warnP + msg.get() );
        }
    }

    @Override
    public Logger setLevel(LoggerLevel level) {
        this.level = level;
        return this;
    }

    @Override
    public LoggerLevel getLevel() {
        return level;
    }

}
