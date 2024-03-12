/*
 * It is free.
 */
package greeter;

import java.util.logging.Logger;

/**
 * The code stripper will break this class.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class BrokenOnPurpose {

    //cs:remove:start
    /**
     * Fresh breaking glass.
     */
    public BrokenOnPurpose() {
    }

    private static final Logger LOG = Logger.getLogger( BrokenOnPurpose.class
            .getName() );
}
