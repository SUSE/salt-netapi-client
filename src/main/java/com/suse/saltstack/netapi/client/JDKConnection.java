package com.suse.saltstack.netapi.client;

import com.suse.saltstack.netapi.config.ClientConfig;
import com.suse.saltstack.netapi.exception.SaltStackException;
import com.suse.saltstack.netapi.parser.SaltStackParser;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Class representation of a connection to SaltStack for issuing API requests using JDK's
 * HttpURLConnection.
 */
public class JDKConnection<T> implements Connection<T> {

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
    public JDKConnection(String endpointIn, SaltStackParser<T> parserIn, ClientConfig configIn) {
        endpoint = endpointIn;
        config = configIn;
        parser = parserIn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getResult(String data) throws SaltStackException {
        return request("POST", data);
    }

    /**
     * Perform HTTP request and parse the result into a given result type.
     *
     * @param method the HTTP method to use
     * @return object of type given by resultType
     * @throws SaltStackException in case of a problem
     */
    private T request(String method, String data)
            throws SaltStackException {
        HttpURLConnection connection = null;
        try {
            // Setup and configure the connection
            connection = RequestFactory.getInstance().initConnection(
                    method, endpoint, config);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

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
                throw new SaltStackException("Response code: " + responseCode);
            }
        } catch (IOException e) {
            throw new SaltStackException(e);
        } finally {
            // Clean up connection and streams
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
