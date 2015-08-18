package com.suse.saltstack.netapi.calls;

/**
 * Possible values for the client parameter in salt netapi calls.
 */
public enum Client {

    LOCAL("local"),
    LOCAL_ASYNC("local_async"),
    WHEEL("wheel"),
    WHEEL_ASYNC("wheel_async"),
    RUNNER("runner"),
    RUNNER_ASYNC("runner_async"),
    SSH("ssh");

    private final String value;

    Client(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
