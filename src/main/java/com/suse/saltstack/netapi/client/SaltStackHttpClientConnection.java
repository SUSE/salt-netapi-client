package com.suse.saltstack.netapi.client;

import com.google.gson.Gson;
import com.suse.saltstack.netapi.config.SaltStackClientConfig;
import com.suse.saltstack.netapi.exception.SaltStackException;
import com.suse.saltstack.netapi.utils.SaltStackClientUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Class representation of a connection to SaltStack for issuing API requests
 * using Apache's HttpClient.
 */
public class SaltStackHttpClientConnection implements SaltStackConnection {

    /** The endpoint. */
    private String endpoint;

    /** The config object. */
    private final SaltStackClientConfig config;

    /**
     * Init a connection to a given SaltStack API endpoint.
     *
     * @param endpointIn the endpoint
     * @param configIn the config
     */
    public SaltStackHttpClientConnection(String endpointIn,
            SaltStackClientConfig configIn) {
        endpoint = endpointIn;
        config = configIn;
    }

    /**
     * Send a POST request and parse the result into object of given {@link java.lang.reflect.Type}.
     *
     * @param resultType the type of the result
     * @param data the data to send (in JSON format)
     * @return object of type given by resultType
     * @throws com.suse.saltstack.netapi.exception.SaltStackException if the request was not successful
     */
    public <T> T getResult(Type resultType, String data) throws SaltStackException {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();

        // Configure proxy if specified on configuration
        String proxyHost = config.getProxyHostname();
        if (proxyHost != null) {
            int proxyPort = Integer.parseInt(config.getProxyPort());
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            httpClientBuilder.setProxy(proxy);

            // Proxy authentication
            String proxyUsername = config.getProxyUsername();
            String proxyPassword = config.getProxyPassword();
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
            String uri = config.getUrl() + endpoint;
            HttpPost httpPost = new HttpPost(uri);
            httpPost.addHeader("Accept", "application/json");

            // POST data
            if (data != null) {
                httpPost.addHeader("Content-Type", "application/json");
                httpPost.setEntity(new StringEntity(data));
            }

            // Token authentication
            String token = config.getToken();
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
                String result = SaltStackClientUtils.streamToString(
                        response.getEntity().getContent());
                return new Gson().fromJson(result, resultType);
            }
        } catch (IOException e) {
            throw new SaltStackException(e);
        }
    }
}
