/*
 * It is free.
 */
package greeter;

/**
 * What a greeter should do.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public interface SayHi {

    /**
     * The format of the greeter should have at least two string parameters such
     * that a greeting of the form 'Good %1$s %2$s', such that a greeting of the
     * kind 'Good evening John' can be expressed.
     *
     * @param name to greet
     * @return a greeting
     */
    default String greet(String name) {
        return format().formatted( timeOfDay(), name );
    }

    /**
     * Get the format for this greeter
     *
     * @return the format
     */
    String format();

    /**
     * Get the time of day as Morning, Afternoon etc.
     *
     * @return the time of day
     */
    String timeOfDay();
}
