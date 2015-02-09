package com.suse.saltstack.netapi.client;

import com.suse.saltstack.netapi.config.SaltStackClientConfig;

/**
 * Implementation of a factory for connections using JDK's HttpURLConnection.
 *
 * @see SaltStackJDKConnection
 */
public class SaltStackJDKConnectionFactory implements SaltStackConnectionFactory {
    /**
     * {@inheritDoc}
     */
    @Override
    public SaltStackJDKConnection create(String endpoint,
            SaltStackClientConfig config) {
        return new SaltStackJDKConnection(endpoint, config);
    }
}
