package com.suse.saltstack.netapi.exception;

/**
 * Exception to be thrown in case of problems parsing service responses.
 */
public class SaltStackParsingException extends SaltStackException {
    /**
     * Constructor expecting a custom cause.
     *
     * @param cause the cause
     */
    public SaltStackParsingException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor expecting a custom message.
     *
     * @param message the message
     */
    public SaltStackParsingException(String message) {
        super(message);
    }

}
