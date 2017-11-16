package com.suse.salt.netapi.client;

import com.suse.salt.netapi.parser.JsonParser;

/**
 * Interface for creating instances of an HTTP async connection implementation.
 */
public interface AsyncConnectionFactory extends AutoCloseable {

    /**
     * Create a new {@link AsyncConnection} for a given endpoint and configuration.
     *
     * @param <T> type of the result as returned by the parser
     * @param endpoint the API endpoint
     * @param parser the parser used for parsing the result
     * @return object representing a connection to the API
     */
    <T> AsyncConnection<T> create(String endpoint, JsonParser<T> parser);
}
