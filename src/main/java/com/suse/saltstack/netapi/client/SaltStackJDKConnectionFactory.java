package com.suse.saltstack.netapi.client;

import com.suse.saltstack.netapi.config.SaltStackClientConfig;
import com.suse.saltstack.netapi.parser.SaltStackParser;

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
    public SaltStackJDKConnection create(String endpoint, SaltStackParser parser, SaltStackClientConfig config) {
        return new SaltStackJDKConnection(endpoint, parser, config);
    }
}
