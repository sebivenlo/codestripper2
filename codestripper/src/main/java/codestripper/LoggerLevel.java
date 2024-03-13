package codestripper;

/**
 * Levels of verbosity.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public enum LoggerLevel {
    /**
     * Silent as in Quiet.
     */
    MUTE,
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

    public boolean lessThan(LoggerLevel other) {
        return this.compareTo( other ) < 0;
    }

    public boolean lessThanOrEqual(LoggerLevel other) {
        return this.compareTo( other ) <= 0;
    }
}
