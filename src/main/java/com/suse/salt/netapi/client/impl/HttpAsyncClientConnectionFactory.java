package com.suse.salt.netapi.client.impl;

import com.suse.salt.netapi.client.AsyncConnectionFactory;
import com.suse.salt.netapi.config.ClientConfig;
import com.suse.salt.netapi.parser.JsonParser;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;

/**
 * Implementation of a factory for connections using Apache's HttpAsyncClient.
 *
 * @see HttpAsyncClientConnection
 */
public class HttpAsyncClientConnectionFactory implements AsyncConnectionFactory {

    /** HTTP client instance */
    final CloseableHttpAsyncClient httpClient;

    /** Salt client configuration */
    final ClientConfig config;

    /**
     * Constructor for creating a connection factory based on the given configuration.
     *
     * @param config the client configuration
     */
    public HttpAsyncClientConnectionFactory(ClientConfig config) {
        this(config, initializeHttpClient(config).build());
    }

    /**
     * Constructor for creating a connection factory based on the given custom HTTP client
     * instance and configuration object.
     *
     * @param config the client configuration
     * @param httpClient a custom HTTP client instance
     */
    public HttpAsyncClientConnectionFactory(ClientConfig config,
            CloseableHttpAsyncClient httpClient) {
        this.config = config;
        this.httpClient = httpClient;
        this.httpClient.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> HttpAsyncClientConnection<T> create(String endpoint, JsonParser<T> parser) {
        return new HttpAsyncClientConnection<>(httpClient, endpoint, parser, config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws Exception {
        httpClient.close();
    }

    /**
     * Initialize HttpAsyncClientBuilder based on the current ClientConfig object.
     *
     * @param config the client configuration
     */
    private static HttpAsyncClientBuilder initializeHttpClient(ClientConfig config) {
        HttpAsyncClientBuilder httpClientBuilder = HttpAsyncClients.custom();
        configure(httpClientBuilder, config);
        configureProxyIfSpecified(httpClientBuilder, config);
        return httpClientBuilder;
    }

    /**
     * Configure the supplied HttpAsyncClientBuilder with defaults and settings from the
     * current ClientConfig object.
     *
     * @param httpClientBuilder the {@link HttpAsyncClientBuilder} to be configured
     * @param config the client configuration
     */
    private static void configure(HttpAsyncClientBuilder httpClientBuilder,
            ClientConfig config) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(config.get(ClientConfig.CONNECT_TIMEOUT))
                .setSocketTimeout(config.get(ClientConfig.SOCKET_TIMEOUT))
                .setCookieSpec(CookieSpecs.STANDARD)
                .build();

        httpClientBuilder.setDefaultRequestConfig(requestConfig);
    }

    /**
     * Configure the HttpAsyncClientBuilder with the proxy settings if specified in the
     * ClientConfig object.
     *
     * @param httpClientBuilder the {@link HttpAsyncClientBuilder} to be configured
     * @param config the client configuration
     */
    private static void configureProxyIfSpecified(HttpAsyncClientBuilder httpClientBuilder,
            ClientConfig config) {
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
}
