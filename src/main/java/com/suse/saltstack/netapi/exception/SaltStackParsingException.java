package com.suse.saltstack.netapi.exception;

/**
 * Exception from parsing a salt-api result.
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
     * @param cause the cause
     */
    public SaltStackParsingException(String message) {
        super(message);
    }

}
