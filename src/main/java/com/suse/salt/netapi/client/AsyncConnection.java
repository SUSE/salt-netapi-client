package com.suse.salt.netapi.client;

import java.util.concurrent.CompletionStage;

/**
 * Interface for different HTTP async connection implementations.
 * @param <T> type of result retrieved using this HTTP connection
 */
public interface AsyncConnection<T> {

    /**
     * Send a GET request and parse the result into object of given type.
     *
     * @return CompletionStage holding object of the given return type T
     */
    CompletionStage<T> get();

    /**
     * Send a POST request and parse the result into object of given type.
     *
     * @param data the data to send (in JSON format)
     * @return CompletionStage holding object of the given return type T
     */
    CompletionStage<T> post(String data);
}
