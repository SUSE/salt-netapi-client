package com.suse.salt.netapi.exception;

/**
 * Exception to be thrown in case of problems with Salt.
 */
public class SaltException extends Exception {

    private static final long serialVersionUID = 3L;

    /**
     * Constructor expecting a custom cause.
     *
     * @param cause the cause
     */
    public SaltException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor expecting a custom message.
     *
     * @param message the message
     */
    public SaltException(String message) {
        super(message);
    }
}
