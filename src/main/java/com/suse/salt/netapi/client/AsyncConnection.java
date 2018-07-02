package com.suse.salt.netapi.client;

import com.suse.salt.netapi.parser.JsonParser;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletionStage;

/**
 * Interface for different HTTP async connection implementations.
 */
public interface AsyncConnection {

    /**
     * Send a GET request and parse the result into object of given type.
     *
     * @param headers headers to pass to the request
     * @return CompletionStage holding object of the given return type T
     */
    <T> CompletionStage<T> get(URI uri, Map<String, String> headers, JsonParser<T> parser);

    /**
     * Send a GET request and parse the result into object of given type.
     *
     * @return CompletionStage holding object of the given return type T
     */
    default <T>  CompletionStage<T> get(URI uri, JsonParser<T> parser) {
        return get(uri, Collections.emptyMap(), parser);
    }

    /**
     * Send a POST request and parse the result into object of given type.
     *
     * @param data    the data to send (in JSON format)
     * @param headers headers to pass to the request
     * @return CompletionStage holding object of the given return type T
     */
    <T> CompletionStage<T> post(URI uri, Map<String, String> headers, String data, JsonParser<T> parser);

    /**
     * Send a POST request and parse the result into object of given type.
     *
     * @param data the data to send (in JSON format)
     * @return CompletionStage holding object of the given return type T
     */
    default <T> CompletionStage<T> post(URI uri, String data, JsonParser<T> parser) {
        return post(uri, Collections.emptyMap(), data, parser);
    }
}
