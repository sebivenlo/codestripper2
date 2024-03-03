/*
 * It is free.
 */
package greeter;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public interface SayHi {

    /**
     * The format of the greeter should have at least two string parameters such
     * that a greeting of the form 'Good %1$s %2$s', such that a greeting of the
     * kind 'Good evening John' can be expressed.
     *
     * @param format
     * @return
     */
    default String greet(String name) {
        return format().formatted( timeOfDay(), name );
    }

    String format();
    String timeOfDay();
}
