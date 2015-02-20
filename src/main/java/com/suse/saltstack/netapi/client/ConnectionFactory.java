package com.suse.saltstack.netapi.client;

import com.suse.saltstack.netapi.config.ClientConfig;
import com.suse.saltstack.netapi.parser.JsonParser;

/**
 * Describes an interface for creating instances of an HTTP connection implementation.
 */
public interface ConnectionFactory {
    /**
     * Create a new {@link Connection} for a given endpoint and configuration.
     *
     * @param endpoint the API endpoint
     * @param config the configuration
     * @return object representing a connection to the API
     */
    <T> Connection<T> create(String endpoint, JsonParser<T> parser,  ClientConfig config);
}
