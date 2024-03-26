package mytinylogger;

/**
 * Levels of verbosity.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public enum LoggerLevel {
    /**
     * Silent as in Quiet.
     */
    MUTE, WARN, ERROR,
    /**
     * Default level
     */
    INFO,
    /**
     * show more info.
     */
    DEBUG,
    /**
     * show most info.
     */
    FINE;

    public boolean greaterEqual(LoggerLevel other) {
        return this.compareTo( other ) >= 0;
    }

}
