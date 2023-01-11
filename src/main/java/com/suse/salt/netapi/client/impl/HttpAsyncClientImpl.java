package com.suse.salt.netapi.client.impl;

import com.suse.salt.netapi.client.AsyncHttpClient;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * AsyncHttpClient implemented with Apache's HttpAsyncClient.
 *
 */
public class HttpAsyncClientImpl implements AsyncHttpClient {

    /** HTTP client instance */
    private final HttpAsyncClient httpClient;

    /**
     * Init a connection to a given Salt API endpoint.
     *
     * @param httpClientIn the HTTP client
     */
    public HttpAsyncClientImpl(HttpAsyncClient httpClientIn) {
        httpClient = httpClientIn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletionStage<T> post(URI uri, Map<String, String> headers, String data, JsonParser<T> parser) {
        return request(uri, headers, data, parser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> CompletionStage<T> get(URI uri, Map<String, String> headers, JsonParser<T> parser) {
        return request(uri, headers, null, parser);
    }

    /**
     * Perform HTTP request and parse the result into a given result type.
     *
     * @param data the data to send with the request
     * @return CompletionStage holding object of type T
     */
    private <T> CompletionStage<T> request(URI uri, Map<String, String> headers, String data, JsonParser<T> parser) {
        return executeRequest(httpClient, prepareRequest(uri, headers, data), parser);
    }

    /**
     * Prepares the HTTP request object creating a POST or GET request depending on if data
     * is supplied or not.
     *
     * @param jsonData json POST data, will use GET if null
     * @return HttpUriRequest object the prepared request
     */
    private <T> HttpUriRequest prepareRequest(URI uri, Map<String, String> headers, String jsonData) {
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
        headers.forEach(httpRequest::addHeader);

        return httpRequest;
    }

    /**
     * Executes a prepared HTTP request using the given client.
     *
     * @param httpClient the client to use for the request
     * @param httpRequest the prepared request to perform
     * @return CompletionStage holding object of type T
     */
    private <T> CompletionStage<T> executeRequest(HttpAsyncClient httpClient,
            HttpUriRequest httpRequest, JsonParser<T> parser) {
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
                    future.completeExceptionally(createSaltException(response));
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
     * Create the appropriate exception for the given HTTP response.
     *
     * @param response HTTP response
     * @return {@link SaltException} instance
     */
    private SaltException createSaltException(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
            return new SaltUserUnauthorizedException(
                    "Salt user does not have sufficient permissions");
        }
        else {
            String content = "";
            try {
                content = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
                        .lines().parallel().collect(Collectors.joining("\n"));
            }
            catch (IOException e) {
                // error trying to get the response body, nothing to do...
            }
            return new SaltException("Response code: " + statusCode + ". Response body:\n" + content);
        }
    }
}
