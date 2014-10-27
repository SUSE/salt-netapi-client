package com.suse.saltstack.netapi.exception;

/**
 * Exception to be thrown in case of problems with SaltStack.
 */
public class SaltStackException extends Exception {

    /**
     * Constructor expecting a custom cause.
     *
     * @param cause the cause
     */
    public SaltStackException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor expecting a custom message.
     *
     * @param cause the cause
     */
    public SaltStackException(String message) {
        super(message);
    }
}
