package mytinylogger;

/**
 * Levels of verbosity.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public enum LoggerLevel {
    /**
     * show most info.
     */
    FINE,
    /**
     * show more info.
     */
    DEBUG,
    /**
     * Default level
     */
    INFO,
    WARN,
    ERROR,
    /**
     * Silent as in Quiet. shut up.
     */
    MUTE;

    public boolean greaterEqual(LoggerLevel current) {
        return this.compareTo( current ) >= 0;
    }

}
