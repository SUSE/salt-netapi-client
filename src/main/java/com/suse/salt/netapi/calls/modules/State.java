package com.suse.salt.netapi.calls.modules;

import com.suse.salt.netapi.calls.LocalCall;

import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * salt.modules.state
 */
public class State {

    private State() { }

    public static LocalCall<Map<String, Object>> apply(List<String> mods) {
        return apply(mods, Optional.empty(), Optional.empty());
    }

    public static LocalCall<Map<String, Object>> apply(String... mods) {
        return apply(Arrays.asList(mods), Optional.empty(), Optional.empty());
    }

    public static LocalCall<Map<String, Object>> apply(List<String> mods,
            Optional<Map<String, Object>> pillar, Optional<Boolean> queue) {
        Map<String, Object> kwargs = new LinkedHashMap<>();
        kwargs.put("mods", mods);
        if (pillar.isPresent()) {
            kwargs.put("pillar", pillar.get());
        }
        if (queue.isPresent()) {
            kwargs.put("queue", queue.get());
        }
        return new LocalCall<>("state.apply", Optional.empty(), Optional.of(kwargs),
                new TypeToken<Map<String, Object>>() { });
    }

    public static LocalCall<Object> showHighstate() {
        return new LocalCall<>("state.show_highstate", Optional.empty(), Optional.empty(),
                new TypeToken<Object>() { });
    }

}
