/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mytinylogger;

import loggerwrapper.Logger;
import static loggerwrapper.LoggerLevel.*;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class Main {

    public static void main(String[] args) {
        Logger l = new DefaultLogger().level( FINE );
        l.info( () -> "Hello world" );
        l.debug( () -> "Hello world" );
        l.fine( () -> "Hello world" );
        l.warn( () -> "Hello world" );
        l.error( () -> "Hello world" );
    }
}
