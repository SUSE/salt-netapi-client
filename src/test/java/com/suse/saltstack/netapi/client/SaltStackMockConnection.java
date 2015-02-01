package com.suse.saltstack.netapi.client;

import com.google.gson.Gson;
import com.suse.saltstack.netapi.exception.SaltStackException;

import java.lang.reflect.Type;

/**
 * Class that implements a mocked connection. Can be configured to return a valid JSON or an Exception.
 */
class SaltStackMockConnection implements SaltStackConnection {
    private final String mockJson;
    private final SaltStackException mockException;

    public SaltStackMockConnection(String mockJson) {
        this.mockJson = mockJson;
        this.mockException = null;
    }

    public SaltStackMockConnection(SaltStackException mockException) {
        this.mockJson = null;
        this.mockException = mockException;
    }

    @Override
    public <T> T getResult(Type resultType, String data) throws SaltStackException {
        if (mockJson != null) {
            return new Gson().fromJson(mockJson, resultType);
        }
        if (mockException != null) {
            throw mockException;
        }
        throw new RuntimeException("Mock not configured properly");
    }
}
