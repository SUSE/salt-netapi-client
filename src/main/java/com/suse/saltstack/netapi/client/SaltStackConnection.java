package com.suse.saltstack.netapi.client;

import com.suse.saltstack.netapi.exception.SaltStackException;

import java.lang.reflect.Type;

/**
 * Describes an interface for different HTTP connection implementations.
 */
public interface SaltStackConnection {
    /**
     * Send a POST request and parse the result into object of given {@link Type}.
     *
     * @param resultType the type of the result
     * @param data the data to send (in JSON format)
     * @return object of type given by resultType
     * @throws SaltStackException if the request was not successful
     */
    <T> T getResult(Type resultType, String data) throws SaltStackException;
}
