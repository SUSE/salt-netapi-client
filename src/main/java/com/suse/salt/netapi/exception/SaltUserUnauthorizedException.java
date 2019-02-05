package com.suse.salt.netapi.exception;

/**
 * Exception for when a user is logged in but not allowed
 * access to the requested resource.
 */
public class SaltUserUnauthorizedException extends SaltException {

    private static final long serialVersionUID = 4L;

    public SaltUserUnauthorizedException(String message) {
        super(message);
    }
}
