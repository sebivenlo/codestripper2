/*
 * It is free.
 */
package greeter;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class Main {

    public static void main(String[] args) {
        Greeter greeter = new Greeter( "Good %1$s %2$s" );
        System.out.println( greeter.greet( "Pieter" ) );
    }
}
