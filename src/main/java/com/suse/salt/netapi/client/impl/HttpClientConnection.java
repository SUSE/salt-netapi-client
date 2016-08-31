package com.suse.salt.netapi.client.impl;

import com.suse.salt.netapi.client.Connection;
import com.suse.salt.netapi.config.ClientConfig;
import com.suse.salt.netapi.exception.SaltException;
import com.suse.salt.netapi.exception.SaltUserUnauthorizedException;
import com.suse.salt.netapi.parser.JsonParser;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;

/**
 * Class representation of a connection to Salt for issuing API requests
 * using Apache's HttpClient.
 * @param <T> type of result retrieved using this HTTP connection
 */
public class HttpClientConnection<T> implements Connection<T> {

    /** The endpoint. */
    private final String endpoint;

    /** The config object. */
    private final ClientConfig config;

    /** The parser to parse the returned Result */
    private final JsonParser<T> parser;

    /**
     * Init a connection to a given Salt API endpoint.
     *
     * @param endpointIn the endpoint
     * @param parserIn the parser
     * @param configIn the config
     */
    public HttpClientConnection(String endpointIn, JsonParser<T> parserIn,
            ClientConfig configIn) {
        endpoint = endpointIn;
        config = configIn;
        parser = parserIn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getResult(String data) throws SaltException {
        return request(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getResult() throws SaltException {
        return request(null);
    }

    /**
     * Perform HTTP request and parse the result into a given result type.
     *
     * @param data the data to send with the request
     * @return object of type T
     * @throws SaltException in case of a problem when executing the request
     */
    private T request(String data) throws SaltException {
        try (CloseableHttpClient httpClient = initializeHttpClient().build()) {
            return executeRequest(httpClient, prepareRequest(data));
        } catch (IOException e) {
            throw new SaltException(e);
        }
    }

    /**
     * Initialize a HttpClientBuilder based on the current ClientConfig object.
     */
    private HttpClientBuilder initializeHttpClient() {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        configureTimeouts(httpClientBuilder);
        configureProxyIfSpecified(httpClientBuilder);
        customizeHttpClient(httpClientBuilder);
        return httpClientBuilder;
    }

    /**
     * Allow subclasses to customize the {@link HttpClientBuilder} as needed.
     *
     * @param httpClientBuilder the {@link HttpClientBuilder} to be customized
     */
    protected void customizeHttpClient(HttpClientBuilder httpClientBuilder) {
    }

    /**
     * Configure the supplied HttpClientBuilder with timeout settings from the current
     * ClientConfig object.
     *
     * @param httpClientBuilder the {@link HttpClientBuilder} to be configured
     */
    private void configureTimeouts(HttpClientBuilder httpClientBuilder) {
        // Timeouts may be specified on configuration
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(config.get(ClientConfig.CONNECT_TIMEOUT))
                .setSocketTimeout(config.get(ClientConfig.SOCKET_TIMEOUT))
                .build();

        httpClientBuilder.setDefaultRequestConfig(requestConfig);
    }

    /**
     * Configure the HttpClientBuilder with the proxy settings if specified in the
     * ClientConfig object.
     *
     * @param httpClientBuilder the {@link HttpClientBuilder} to be configured
     */
    private void configureProxyIfSpecified(HttpClientBuilder httpClientBuilder) {
        String proxyHost = config.get(ClientConfig.PROXY_HOSTNAME);
        if (proxyHost != null) {
            int proxyPort = config.get(ClientConfig.PROXY_PORT);

            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            httpClientBuilder.setProxy(proxy);

            String proxyUsername = config.get(ClientConfig.PROXY_USERNAME);
            String proxyPassword = config.get(ClientConfig.PROXY_PASSWORD);

            // Proxy authentication
            if (proxyUsername != null && proxyPassword != null) {
                CredentialsProvider credentials = new BasicCredentialsProvider();
                credentials.setCredentials(
                        new AuthScope(proxyHost, proxyPort),
                        new UsernamePasswordCredentials(proxyUsername, proxyPassword));
                httpClientBuilder.setDefaultCredentialsProvider(credentials);
            }
        }
    }

    /**
     * Prepares the HTTP request object creating a POST or GET request depending on if data
     * is supplied or not.
     *
     * @param jsonData json POST data, will use GET if null
     * @return HttpUriRequest object the prepared request
     * @throws UnsupportedEncodingException when charset is not available
     */
    private HttpUriRequest prepareRequest(String jsonData)
            throws UnsupportedEncodingException {
        URI uri = config.get(ClientConfig.URL).resolve(endpoint);
        HttpUriRequest httpRequest;
        if (jsonData != null) {
            // POST data
            HttpPost httpPost = new HttpPost(uri);
            httpPost.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            httpPost.setEntity(new StringEntity(jsonData));
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
     * @throws SaltException if HTTP status code is not as expected (200 or 202)
     * @throws IOException in case of problems executing the request
     */
    private T executeRequest(CloseableHttpClient httpClient, HttpUriRequest httpRequest)
            throws SaltException, IOException {
        try (CloseableHttpResponse response = httpClient.execute(httpRequest)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK ||
                    statusCode == HttpStatus.SC_ACCEPTED) {
                // Parse result type from the returned JSON
                return parser.parse(response.getEntity().getContent());
            } else {
                throw createSaltException(statusCode);
            }
        }
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
