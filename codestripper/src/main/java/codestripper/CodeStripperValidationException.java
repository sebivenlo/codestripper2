package codestripper;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class CodeStripperValidationException extends Exception {

    String longMessage;

    public CodeStripperValidationException(String longMessage, String message,
            Throwable cause) {
        super( message, cause );
        this.longMessage = longMessage;
    }

    public CodeStripperValidationException(String message) {
        super( message );
        this.longMessage = message;
    }

    public CodeStripperValidationException(String longMessage, String message) {
        super( message );
        this.longMessage = longMessage;
    }

    public CodeStripperValidationException(String message,
            Throwable cause) {
        this( message, message, cause );
    }

    public String getLongMessage() {
        return longMessage;
    }

    public CodeStripperValidationException setLongMessage(String longMessage) {
        this.longMessage = longMessage;
        return this;
    }
}
