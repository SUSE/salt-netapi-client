package com.suse.saltstack.netapi.client.impl;

import com.suse.saltstack.netapi.client.Connection;
import com.suse.saltstack.netapi.config.ClientConfig;

import com.suse.saltstack.netapi.exception.SaltException;
import com.suse.saltstack.netapi.parser.JsonParser;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Class representation of a connection to SaltStack for issuing API requests using JDK's
 * HttpURLConnection.
 * @param <T> type of result retrieved using this HTTP connection
 */
public class JDKConnection<T> implements Connection<T> {

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
     * @param parserIn the parser to be used for parsing the result
     * @param configIn the config
     */
    public JDKConnection(String endpointIn, JsonParser<T> parserIn, ClientConfig configIn) {
        endpoint = endpointIn;
        config = configIn;
        parser = parserIn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getResult(String data) throws SaltException {
        return request("POST", data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getResult() throws SaltException {
        return request("GET", null);
    }

    /**
     * Perform HTTP request and parse the result into a given result type.
     *
     * @param method the HTTP method to use
     * @return object of type given by resultType
     * @throws SaltException in case of a problem
     */
    private T request(String method, String data)
            throws SaltException {
        HttpURLConnection connection = null;
        try {
            // Setup and configure the connection
            connection = RequestFactory.getInstance().initConnection(
                    method, endpoint, config);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Timeouts may be specified on configuration
            int connectTimeout = config.get(ClientConfig.CONNECT_TIMEOUT);
            if (connectTimeout >= 0) {
                connection.setConnectTimeout(connectTimeout);
            }

            int socketTimeout = config.get(ClientConfig.SOCKET_TIMEOUT);
            if (socketTimeout >= 0) {
                connection.setReadTimeout(socketTimeout);
            }

            // Send data in case we have some
            if (data != null) {
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Content-Length",
                        Integer.toString(data.getBytes().length));

                // Actually write the bytes
                DataOutputStream outputStream = new DataOutputStream(
                        connection.getOutputStream());
                outputStream.writeBytes(data);
                outputStream.flush();
                outputStream.close();
            }

            // React depending on the result of the request
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK ||
                    responseCode == HttpURLConnection.HTTP_ACCEPTED) {
                return parser.parse(connection.getInputStream());
            } else {
                // Request was not successful
                throw new SaltException("Response code: " + responseCode);
            }
        } catch (IOException e) {
            throw new SaltException(e);
        } finally {
            // Clean up connection and streams
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
