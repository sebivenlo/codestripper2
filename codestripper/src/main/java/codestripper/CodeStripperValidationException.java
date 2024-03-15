package codestripper;

/**
 * Exception to fail build when thrown.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class CodeStripperValidationException extends Exception {

    String longMessage;

    /**
     * Create exception with a long message
     *
     * @param longMessage to use
     * @param message the message
     * @param cause sic
     */
    public CodeStripperValidationException(String longMessage, String message,
            Throwable cause) {
        super( message, cause );
        this.longMessage = longMessage;
    }

    /**
     *
     * @param message message only
     */
    public CodeStripperValidationException(String message) {
        super( message );
        this.longMessage = message;
    }

    /**
     * With tow texts.
     *
     * @param longMessage sic
     * @param message sic
     */
    public CodeStripperValidationException(String longMessage, String message) {
        super( message );
        this.longMessage = longMessage;
    }

    /**
     * Message and cause.
     *
     * @param message sic
     * @param cause sic
     */
    public CodeStripperValidationException(String message,
            Throwable cause) {
        this( message, message, cause );
    }

    /**
     * Get the long message.
     *
     * @return long message
     */
    public String getLongMessage() {
        return longMessage;
    }

    /**
     * Set the long message
     *
     * @param longMessage to set
     * @return this
     */
    public CodeStripperValidationException setLongMessage(String longMessage) {
        this.longMessage = longMessage;
        return this;
    }
}