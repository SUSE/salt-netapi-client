package com.suse.saltstack.netapi.client;

import com.suse.saltstack.netapi.config.SaltStackClientConfig;

/**
 * Describes an interface for creating instances of an HTTP connection implementation.
 */
public interface SaltStackConnectionFactory {
    SaltStackConnection create(String endpointIn, SaltStackClientConfig configIn);
}
