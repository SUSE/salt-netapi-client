package com.suse.salt.netapi.client.impl;

import com.suse.salt.netapi.client.AsyncConnection;
import com.suse.salt.netapi.config.ClientConfig;
import com.suse.salt.netapi.exception.SaltException;
import com.suse.salt.netapi.exception.SaltUserUnauthorizedException;
import com.suse.salt.netapi.parser.JsonParser;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.nio.client.HttpAsyncClient;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Representation of a connection to Salt for issuing API requests using Apache's
 * HttpAsyncClient.
 *
 * @param <T> type of result retrieved using this HTTP connection
 */
public class HttpAsyncClientConnection<T> implements AsyncConnection<T> {

    /** HTTP client instance */
    private final HttpAsyncClient httpClient;

    /** Endpoint */
    private final String endpoint;

    /** Configuration */
    private final ClientConfig config;

    /** Parser to parse the returned result */
    private final JsonParser<T> parser;

    /**
     * Init a connection to a given Salt API endpoint.
     *
     * @param httpClientIn the HTTP client
     * @param endpointIn the endpoint
     * @param parserIn the parser
     * @param configIn the config
     */
    public HttpAsyncClientConnection(HttpAsyncClient httpClientIn, String endpointIn,
            JsonParser<T> parserIn, ClientConfig configIn) {
        httpClient = httpClientIn;
        endpoint = endpointIn;
        config = configIn;
        parser = parserIn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletionStage<T> post(String data) {
        return request(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletionStage<T> get() {
        return request(null);
    }

    /**
     * Perform HTTP request and parse the result into a given result type.
     *
     * @param data the data to send with the request
     * @return CompletionStage holding object of type T
     */
    private CompletionStage<T> request(String data) {
        return executeRequest(httpClient, prepareRequest(data));
    }

    /**
     * Prepares the HTTP request object creating a POST or GET request depending on if data
     * is supplied or not.
     *
     * @param jsonData json POST data, will use GET if null
     * @return HttpUriRequest object the prepared request
     */
    private HttpUriRequest prepareRequest(String jsonData) {
        URI uri = config.get(ClientConfig.URL).resolve(endpoint);
        HttpUriRequest httpRequest;
        if (jsonData != null) {
            // POST data
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setEntity(new StringEntity(jsonData, ContentType.APPLICATION_JSON));
            httpRequest = httpPost;
        } else {
            // GET request
            httpRequest = new HttpGet(uri);
        }
        httpRequest.addHeader(HttpHeaders.ACCEPT, "application/json");

        // Token authentication
        String token = config.get(ClientConfig.TOKEN);
        if (token != null) {
            httpRequest.addHeader("X-Auth-Token", token);
        }

        return httpRequest;
    }

    /**
     * Executes a prepared HTTP request using the given client.
     *
     * @param httpClient the client to use for the request
     * @param httpRequest the prepared request to perform
     * @return CompletionStage holding object of type T
     */
    private CompletionStage<T> executeRequest(HttpAsyncClient httpClient,
            HttpUriRequest httpRequest) {
        CompletableFuture<T> future = new CompletableFuture<>();
        httpClient.execute(httpRequest, new FutureCallback<HttpResponse>() {
            @Override
            public void failed(Exception e) {
                future.completeExceptionally(e);
            }

            @Override
            public void completed(HttpResponse response) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK ||
                        statusCode == HttpStatus.SC_ACCEPTED) {

                    // Parse result type from the returned JSON
                    try {
                        T result = parser.parse(response.getEntity().getContent());
                        future.complete(result);
                    } catch (Exception e) {
                        future.completeExceptionally(e);
                    }
                } else {
                    future.completeExceptionally(createSaltException(statusCode));
                }
            }

            @Override
            public void cancelled() {
                future.cancel(false);
            }
        });

        return future;
    }

    /**
     * Create the appropriate exception for the given HTTP status code.
     *
     * @param statusCode HTTP status code
     * @return {@link SaltException} instance
     */
    private SaltException createSaltException(int statusCode) {
        if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
            return new SaltUserUnauthorizedException(
                    "Salt user does not have sufficient permissions");
        }
        return new SaltException("Response code: " + statusCode);
    }
}
