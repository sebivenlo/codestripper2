/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mytinylogger;

import java.io.PrintStream;
import java.util.function.Supplier;
import static mytinylogger.LoggerLevel.*;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class Logger {

    LoggerLevel level = LoggerLevel.MUTE;

    PrintStream out;

    public Logger(PrintStream out) {
        this.out = out;
    }

    public Logger() {
        this( System.out );
    }

    static String errorP = "[\033[31;1mERROR\033[m] ";
    static String infoP = "[\033[34;1mINFO\033[m] ";
    static String warnP = "[\033[1;33mWARNING\033[37;1m]\033[m ";
    static String debugP = "[\033[35;1mDEBUG\033[m] ";
    static String fineP = "[\033[32;1mFINER\033[m] ";

    public void debug(Supplier<String> msg) {
        if ( level.greaterEqual( DEBUG ) ) {
            out.println( debugP + msg.get() );
        }
    }

    public void error(Supplier<String> msg) {
        if ( level.greaterEqual( ERROR ) ) {
            out.println( errorP + msg.get() );
        }
    }

    public void fine(Supplier<String> msg) {
        if ( level.greaterEqual( FINE ) ) {
            out.println( fineP + msg.get() );
        }
    }

    public void info(Supplier<String> msg) {
        if ( level.greaterEqual( INFO ) ) {
            out.println( infoP + msg.get() );
        }
    }

    public void warn(Supplier<String> msg) {
        if ( level.greaterEqual( WARN ) ) {
            out.println( warnP + msg.get() );
        }
    }

    public Logger level(LoggerLevel level) {
        this.level = level;
        return this;
    }

    public LoggerLevel level() {
        return level;
    }

}
