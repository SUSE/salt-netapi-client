package com.suse.saltstack.netapi.exception;

/**
 * Exception to be thrown in case of problems with Salt.
 */
public class SaltException extends Exception {

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
