package com.suse.salt.netapi.exception;

import java.io.IOException;

/**
 * Exception to be thrown in case of problems parsing service responses.
 */
public class ParsingException extends IOException {

    private static final long serialVersionUID = 2L;

    /**
     * Constructor expecting a custom cause.
     *
     * @param cause the cause
     */
    public ParsingException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor expecting a custom message.
     *
     * @param message the message
     */
    public ParsingException(String message) {
        super(message);
    }
}
