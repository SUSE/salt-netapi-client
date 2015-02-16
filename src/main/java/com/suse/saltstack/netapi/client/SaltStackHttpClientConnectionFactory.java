package com.suse.saltstack.netapi.client;

import com.suse.saltstack.netapi.config.SaltStackClientConfig;
import com.suse.saltstack.netapi.parser.SaltStackParser;

/**
 * Implementation of a factory for connections using Apache's HttpClient.
 *
 * @see com.suse.saltstack.netapi.client.SaltStackHttpClientConnection
 */
public class SaltStackHttpClientConnectionFactory implements SaltStackConnectionFactory {
    /**
     * {@inheritDoc}
     */
    @Override
    public SaltStackHttpClientConnection create(String endpoint,
            SaltStackParser parser, SaltStackClientConfig config) {
        return new SaltStackHttpClientConnection(endpoint, parser, config);
    }
}
