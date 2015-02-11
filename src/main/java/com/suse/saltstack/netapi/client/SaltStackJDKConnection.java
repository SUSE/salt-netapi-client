package com.suse.saltstack.netapi.client;

import com.suse.saltstack.netapi.config.SaltStackClientConfig;
import com.suse.saltstack.netapi.exception.SaltStackException;
import com.suse.saltstack.netapi.parser.SaltStackParser;
import com.suse.saltstack.netapi.utils.SaltStackClientUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;

/**
 * Class representation of a connection to SaltStack for issuing API requests using JDK's
 * HttpURLConnection.
 */
public class SaltStackJDKConnection implements SaltStackConnection {

    /** The endpoint. */
    private String endpoint;

    /** The config object. */
    private final SaltStackClientConfig config;

    /** The parser to parse the returned Result */
    private SaltStackParser parser;

    /**
     * Init a connection to a given SaltStack API endpoint.
     *
     * @param endpointIn the endpoint
     * @param configIn the config
     */
    public SaltStackJDKConnection(String endpointIn, SaltStackParser parserIn, SaltStackClientConfig configIn) {
        endpoint = endpointIn;
        config = configIn;
        parser = parserIn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getResult(Class<T> resultType, String data) throws SaltStackException {
        return request(resultType, "POST", data);
    }

    /**
     * Perform HTTP request and parse the result into a given result type.
     *
     * @param resultType the type of the result
     * @param method the HTTP method to use
     * @return object of type given by resultType
     * @throws SaltStackException in case of a problem
     */
    private <T> T request(Type resultType, String method, String data)
            throws SaltStackException {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            // Setup and configure the connection
            connection = SaltStackRequestFactory.getInstance().initConnection(
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
                @SuppressWarnings("unchecked")
                T result = (T)parser.parse(connection.getInputStream());
                return result;
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
            SaltStackClientUtils.closeQuietly(inputStream);
        }
    }
}
