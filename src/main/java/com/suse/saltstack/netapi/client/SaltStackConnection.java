package com.suse.saltstack.netapi.client;

import com.suse.saltstack.netapi.exception.SaltStackException;

import java.lang.reflect.Type;

/**
 * Describes an interface for different HTTP connection implementations.
 */
public interface SaltStackConnection {
    <T> T getResult(Type resultType, String data) throws SaltStackException;
}
