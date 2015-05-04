package com.suse.saltstack.netapi.exception;

/**
 * Exception for when a user is logged in but not allowed
 * access to the requested resource.
 */
public class SaltUserUnauthorizedException extends SaltStackException {

    public SaltUserUnauthorizedException(String message) {
        super(message);
    }
}
