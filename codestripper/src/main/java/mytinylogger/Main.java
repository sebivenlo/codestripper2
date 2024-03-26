package mytinylogger;

import static mytinylogger.LoggerLevel.*;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class Main {

    public static void main(String[] args) {
        var l = new Logger().level( FINE );
        l.info( () -> "Hello world" );
        l.debug( () -> "Hello world" );
        l.fine( () -> "Hello world" );
        l.warn( () -> "Hello world" );
        l.error( () -> "Hello world" );
    }
}
