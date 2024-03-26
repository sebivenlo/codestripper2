/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mytinylogger;

import java.io.PrintStream;
import java.util.EnumMap;
import java.util.Map;
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

    static final String errorP = "[\033[31;1mERROR\033[m] ";
    static final String infoP = "[\033[34;1mINFO\033[m] ";
    static final String warnP = "[\033[1;33mWARNING\033[37;1m]\033[m ";
    static final String debugP = "[\033[35;1mDEBUG\033[m] ";
    static final String fineP = "[\033[32;1mFINER\033[m] ";

    static final Map<LoggerLevel, String> flags = new EnumMap<>( Map.of(
            INFO, infoP,
            WARN, warnP,
            DEBUG, debugP,
            FINE, fineP,
            ERROR, errorP,
            MUTE, "" )
    );

    public void logAtLevel(LoggerLevel level, Supplier<String> msg) {
        if ( level == MUTE ) {
            return;
        }
        if ( level.greaterEqual( level() ) ) {
            out.println( flags.get( level ) + msg.get() );
        }
    }

    public void debug(Supplier<String> msg) {
        logAtLevel( DEBUG, msg );
    }

    public void error(Supplier<String> msg) {
        logAtLevel( ERROR, msg );
    }

    public void fine(Supplier<String> msg) {
        logAtLevel( FINE, msg );
    }

    public void info(Supplier<String> msg) {
        logAtLevel( INFO, msg );
    }

    public void warn(Supplier<String> msg) {
        logAtLevel( WARN, msg );
    }

    public Logger level(LoggerLevel level) {
        this.level = level;
        return this;
    }

    public LoggerLevel level() {
        return level;
    }

}
