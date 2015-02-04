package com.suse.saltstack.netapi.client;

import com.suse.saltstack.netapi.config.SaltStackClientConfig;

/**
 * Implementation of a factory for connections using Apache's HttpClient.
 *
 * @see com.suse.saltstack.netapi.client.SaltStackHttpClientConnection
 */
public class SaltStackHttpClientConnectionFactory implements SaltStackConnectionFactory {
    @Override
    public SaltStackHttpClientConnection create(String endpoint,
                                         SaltStackClientConfig config) {
        return new SaltStackHttpClientConnection(endpoint, config);
    }
}
