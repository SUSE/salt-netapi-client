package com.suse.salt.netapi.client;

import com.suse.salt.netapi.parser.JsonParser;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletionStage;

/**
 * Simple abstraction over async http operations needed by the salt client.
 */
public interface AsyncHttpClient {

    /**
     * Send a GET request and parse the result into object of given type.
     *
     * @param headers headers to pass to the request
     * @param parser parser to use for the response json
     * @param uri uri to make the http request to
     * @param <T> return type the response json will be parsed into
     * @return CompletionStage holding object of the given return type T
     */
    <T> CompletionStage<T> get(URI uri, Map<String, String> headers, JsonParser<T> parser);

    /**
     * Send a GET request and parse the result into object of given type.
     *
     * @param <T> return type the response json will be parsed into
     * @param uri uri to make the http request to
     * @param parser parser to use for the response json
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
     * @param uri uri to make the http request to
     * @param <T> return type the response json will be parsed into
     * @param parser parser to use for the response json
     * @return CompletionStage holding object of the given return type T
     */
    <T> CompletionStage<T> post(URI uri, Map<String, String> headers, String data, JsonParser<T> parser);

    /**
     * Send a POST request and parse the result into object of given type.
     *
     * @param data the data to send (in JSON format)
     * @param <T> return type the response json will be parsed into
     * @param uri uri to make the http request to
     * @param parser parser to use for the response json
     * @return CompletionStage holding object of the given return type T
     */
    default <T> CompletionStage<T> post(URI uri, String data, JsonParser<T> parser) {
        return post(uri, Collections.emptyMap(), data, parser);
    }
}
