package com.suse.salt.netapi.calls.modules;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.gson.reflect.TypeToken;
import com.suse.salt.netapi.calls.LocalCall;

/**
 * salt.modules.minion
 *
 * https://docs.saltstack.com/en/latest/ref/modules/all/salt.modules.minion.html
 */
public class Minion {

    private Minion() { }

    public static LocalCall<Map<String, Set<String>>> list() {
        return new LocalCall<>("minion.list", Optional.empty(), Optional.empty(),
                new TypeToken<Map<String, Set<String>>>(){});
    }

    public static LocalCall<Map<String, Object>> kill() {
        return new LocalCall<>("minion.kill", Optional.empty(), Optional.empty(),
                new TypeToken<Map<String, Object>>(){});
    }

    public static LocalCall<Map<String, Object>> restart() {
        return new LocalCall<>("minion.restart", Optional.empty(), Optional.empty(),
                new TypeToken<Map<String, Object>>(){});
    }
}
