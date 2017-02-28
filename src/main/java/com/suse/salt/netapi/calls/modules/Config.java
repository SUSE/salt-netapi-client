package com.suse.salt.netapi.calls.modules;

import com.google.gson.reflect.TypeToken;
import com.suse.salt.netapi.calls.LocalCall;

import java.util.Arrays;
import java.util.Optional;

/**
 * Returns configuration information from minions.
 */
public class Config {

    /** The configuration key for the master's hostname. */
    public static final String MASTER = "master";

    private Config() { }

    /**
     * Returns a configuration parameter.
     * @param key the parameter name
     * @return the {@link LocalCall} object to make the call
     */
    public static LocalCall<String> get(String key) {
        return new LocalCall<>(
            "config.get",
            Optional.of(Arrays.asList(key)),
            Optional.empty(),
            new TypeToken<String>() { });
    }
}
