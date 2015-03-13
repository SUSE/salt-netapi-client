package com.suse.saltstack.netapi.client.impl;

import com.suse.saltstack.netapi.client.ConnectionFactory;
import com.suse.saltstack.netapi.config.ClientConfig;
import com.suse.saltstack.netapi.parser.JsonParser;

/**
 * Implementation of a factory for connections using Apache's HttpClient.
 *
 * @see HttpClientConnection
 */
public class HttpClientConnectionFactory implements ConnectionFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> HttpClientConnection<T> create(String endpoint,
            JsonParser<T> parser, ClientConfig config) {
        return new HttpClientConnection<>(endpoint, parser, config);
    }
}
