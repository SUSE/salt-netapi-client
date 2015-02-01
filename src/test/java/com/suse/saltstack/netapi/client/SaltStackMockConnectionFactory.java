package com.suse.saltstack.netapi.client;

import com.suse.saltstack.netapi.config.SaltStackClientConfig;
import com.suse.saltstack.netapi.exception.SaltStackException;

/**
 * Class that implements a factory to a mocked connection.
 */
class SaltStackMockConnectionFactory implements SaltStackConnectionFactory {
    private final SaltStackMockConnection connection;

    public SaltStackMockConnectionFactory(String mockJson) {
        this.connection = new SaltStackMockConnection(mockJson);
    }

    public SaltStackMockConnectionFactory(SaltStackException mockException) {
        this.connection = new SaltStackMockConnection(mockException);
    }

    @Override
    public SaltStackConnection create(String endpointIn, SaltStackClientConfig configIn) {
        return connection;
    }
}
