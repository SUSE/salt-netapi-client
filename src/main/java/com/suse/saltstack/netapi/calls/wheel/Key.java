package com.suse.saltstack.netapi.calls.wheel;

import com.google.gson.reflect.TypeToken;
import com.suse.saltstack.netapi.calls.WheelCall;
import com.suse.saltstack.netapi.datatypes.Keys;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * salt.wheel.key
 */
public class Key {

    /**
     * Matching key fingerprints as returned by "key.finger".
     */
    public static class Minions {
        private Map<String, String> minions;

        public Map<String, String> getMinions() {
            return minions;
        }
    }

    private Key() {
    }

    private static final WheelCall<Keys> LIST_ALL =
            new WheelCall<>("key.list_all", Optional.empty(), new TypeToken<Keys>(){});

    public static WheelCall<Minions> finger(String match) {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("match", match);
        return new WheelCall<>("key.finger", Optional.of(args),
                new TypeToken<Minions>(){});
    }

    public static WheelCall<Object> accept(String match) {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("match", match);
        return new WheelCall<>("key.accept", Optional.of(args), new TypeToken<Object>(){});
    }

    public static WheelCall<Object> delete(String match) {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("match", match);
        return new WheelCall<>("key.delete", Optional.of(args), new TypeToken<Object>(){});
    }

    public static WheelCall<Object> reject(String match) {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("match", match);
        return new WheelCall<>("key.reject", Optional.of(args), new TypeToken<Object>(){});
    }

    public static WheelCall<Keys> listAll() {
        return LIST_ALL;
    }
}
