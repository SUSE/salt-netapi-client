package com.suse.salt.netapi.calls;

/**
 * Possible values for the client parameter in salt netapi calls.
 */
public enum Client {

    LOCAL("local"),
    LOCAL_ASYNC("local_async"),
    LOCAL_BATCH("local_batch"),
    RUNNER("runner"),
    RUNNER_ASYNC("runner_async"),
    SSH("ssh"),
    WHEEL("wheel"),
    WHEEL_ASYNC("wheel_async");

    private final String value;

    Client(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
