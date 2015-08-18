package com.suse.saltstack.netapi.calls.modules;

import com.google.gson.reflect.TypeToken;
import com.suse.saltstack.netapi.calls.LocalCall;

import java.util.Optional;

/**
 * salt.modules.test
 */
public class Test {

    private static final LocalCall<Boolean> PING =
            new LocalCall<>("test.ping", Optional.empty(), Optional.empty(),
            new TypeToken<Boolean>(){});

    public static LocalCall<Boolean> ping() {
        return PING;
    }
}
