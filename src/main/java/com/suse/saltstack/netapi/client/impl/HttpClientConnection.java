package com.suse.saltstack.netapi.client.impl;

import com.suse.saltstack.netapi.client.Connection;
import com.suse.saltstack.netapi.config.ClientConfig;
import com.suse.saltstack.netapi.exception.SaltStackException;
import com.suse.saltstack.netapi.parser.JsonParser;
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
import java.net.URI;

/**
 * Class representation of a connection to SaltStack for issuing API requests
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
     * Init a connection to a given SaltStack API endpoint.
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
    public T getResult(String data) throws SaltStackException {
        return request(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getResult() throws SaltStackException {
        return request(null);
    }

    /**
     * Perform HTTP request and parse the result into a given result type.
     *
     * @param data the data to send with the request
     * @return object of type T
     * @throws SaltStackException in case of a problem
     */
    private T request(String data) throws SaltStackException {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();

        // Timeouts may be specified on configuration
        RequestConfig reqconfig = RequestConfig.custom()
                .setConnectTimeout(config.get(CONNECT_TIMEOUT))
                .setSocketTimeout(config.get(SOCKET_TIMEOUT))
                .build();

        httpClientBuilder.setDefaultRequestConfig(reqconfig);

        // Configure proxy if specified on configuration
        String proxyHost = config.get(ClientConfig.PROXY_HOSTNAME);
        if (proxyHost != null) {
            int proxyPort = config.get(ClientConfig.PROXY_PORT);
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            httpClientBuilder.setProxy(proxy);

            // Proxy authentication
            String proxyUsername = config.get(ClientConfig.PROXY_USERNAME);
            String proxyPassword = config.get(ClientConfig.PROXY_PASSWORD);
            if (proxyUsername != null && proxyPassword != null) {
                CredentialsProvider credentials = new BasicCredentialsProvider();
                credentials.setCredentials(
                        new AuthScope(proxyHost, proxyPort),
                        new UsernamePasswordCredentials(proxyUsername, proxyPassword));
                httpClientBuilder.setDefaultCredentialsProvider(credentials);
            }
        }

        try (CloseableHttpClient httpClient = httpClientBuilder.build()) {
            // Prepare request
            URI uri = config.get(ClientConfig.URL).resolve(endpoint);

            HttpUriRequest httpRequest = null;
            if (data != null) {
                // POST data
                HttpPost httpPost = new HttpPost(uri);
                httpPost.addHeader("Content-Type", "application/json");
                httpPost.setEntity(new StringEntity(data));
                httpRequest = httpPost;
            } else {
                // GET request
                httpRequest = new HttpGet(uri);
            }

            httpRequest.addHeader("Accept", "application/json");

            // Token authentication
            String token = config.get(ClientConfig.TOKEN);
            if (token != null) {
                httpRequest.addHeader("X-Auth-Token", token);
            }

            // Execute request
            try (CloseableHttpResponse response = httpClient.execute(httpRequest)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK &&
                        statusCode != HttpStatus.SC_ACCEPTED) {
                    throw new SaltStackException("Response code: " + statusCode);
                }

                // Parse result type from the returned JSON
                return parser.parse(response.getEntity().getContent());
            }
        } catch (IOException e) {
            throw new SaltStackException(e);
        }
    }
}
