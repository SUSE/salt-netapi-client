package com.suse.saltstack.netapi.client;

import com.suse.saltstack.netapi.config.SaltStackClientConfig;

/**
 * Implementation of a factory for connections using JDK's HttpURLConnection.
 *
 * @see com.suse.saltstack.netapi.client.SaltStackJdkConnection
 */
public class SaltStackJdkConnectionFactory implements SaltStackConnectionFactory {
    @Override
    public SaltStackJdkConnection create(String endpointIn, SaltStackClientConfig configIn) {
        return new SaltStackJdkConnection(endpointIn, configIn);
    }
}
