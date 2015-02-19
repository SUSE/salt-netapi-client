package com.suse.saltstack.netapi.client;

import com.suse.saltstack.netapi.config.ClientConfig;
import static com.suse.saltstack.netapi.config.ClientConfig.*;
import com.suse.saltstack.netapi.exception.SaltStackException;
import com.suse.saltstack.netapi.parser.SaltStackParser;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
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
 */
public class HttpClientConnection<T> implements Connection<T> {

    /** The endpoint. */
    private String endpoint;

    /** The config object. */
    private final ClientConfig config;

    /** The parser to parse the returned Result */
    private SaltStackParser<T> parser;

    /**
     * Init a connection to a given SaltStack API endpoint.
     *
     * @param endpointIn the endpoint
     * @param configIn the config
     */
    public HttpClientConnection(String endpointIn, SaltStackParser<T> parserIn,
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
        HttpClientBuilder httpClientBuilder = HttpClients.custom();

        // Configure proxy if specified on configuration
        String proxyHost = config.get(PROXY_HOSTNAME);
        if (proxyHost != null) {
            int proxyPort = config.get(PROXY_PORT);
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            httpClientBuilder.setProxy(proxy);

            // Proxy authentication
            String proxyUsername = config.get(PROXY_USERNAME);
            String proxyPassword = config.get(PROXY_PASSWORD);
            if (proxyUsername != null && proxyPassword != null) {
                CredentialsProvider credentials = new BasicCredentialsProvider();
                credentials.setCredentials(
                        new AuthScope(proxyHost, proxyPort),
                        new UsernamePasswordCredentials(proxyUsername, proxyPassword));
                httpClientBuilder.setDefaultCredentialsProvider(credentials);
            }
        }

        try (CloseableHttpClient httpClient = httpClientBuilder.build()) {
            // Prepare POST request
            URI uri = config.get(URL).resolve(endpoint);
            HttpPost httpPost = new HttpPost(uri);
            httpPost.addHeader("Accept", "application/json");

            // POST data
            if (data != null) {
                httpPost.addHeader("Content-Type", "application/json");
                httpPost.setEntity(new StringEntity(data));
            }

            // Token authentication
            String token = config.get(TOKEN);
            if (token != null) {
                httpPost.addHeader("X-Auth-Token", token);
            }

            // Execute request
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
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
