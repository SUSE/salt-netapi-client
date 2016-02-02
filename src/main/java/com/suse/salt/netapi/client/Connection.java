package com.suse.salt.netapi.client;

import com.suse.salt.netapi.exception.SaltException;

import java.lang.reflect.Type;

/**
 * Describes an interface for different HTTP connection implementations.
 * @param <T> type of result retrieved using this HTTP connection
 */
public interface Connection<T> {

    /**
     * Send a GET request and parse the result into object of given {@link Type}.
     *
     * @return object of type given by resultType
     * @throws SaltException if the request was not successful
     */
    T getResult() throws SaltException;

    /**
     * Send a POST request and parse the result into object of given {@link Type}.
     *
     * @param data the data to send (in JSON format)
     * @return object of type given by resultType
     * @throws SaltException if the request was not successful
     */
    T getResult(String data) throws SaltException;
}
