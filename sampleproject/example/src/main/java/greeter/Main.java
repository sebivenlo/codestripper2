/*
 * It is free.
 */
package greeter;

/**
 * Short.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class Main {

    /**
     * Who does not need me?
     *
     * @param args not used
     */
    public static void main(String[] args) {
        //cs:comment:start
        Greeter greeter = new Greeter( "Good %1$s %2$s" );
        System.out.println( greeter.greet( "Pieter" ) );
        //cs:comment:end
    }

    private Main() {
    }

}
