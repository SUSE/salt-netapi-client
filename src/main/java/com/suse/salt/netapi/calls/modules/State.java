package com.suse.salt.netapi.calls.modules;

import com.google.gson.reflect.TypeToken;
import com.suse.salt.netapi.calls.LocalCall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * salt.modules.state
 */
public class State {

    private State() {
    }

    public static LocalCall<Map<String, Object>> apply(List<String> mods) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("mods", mods);
        return new LocalCall<>("state.apply", Optional.empty(), Optional.of(args),
                new TypeToken<Map<String, Object>>() {});
    }

    public static LocalCall<Map<String, Object>> apply(String... mods) {
        return apply(Arrays.asList(mods));
    }

    public static LocalCall<Map<String, Object>> apply(String mod, 
            Map<String, String> pillarArgs) {
        if (pillarArgs == null || pillarArgs.isEmpty()) {
            return apply(new ArrayList<String>() {
                {
                    add(mod);
                }
            });
        }
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("mods", mod);

        if (pillarArgs != null && !pillarArgs.isEmpty()) {
            Map<String, String> pillarData = new HashMap<>();
            for (Entry<String, String> entry : pillarArgs.entrySet()) {
                pillarData.put(entry.getKey(), entry.getValue());
            }
            args.put("pillar", pillarData);
        }

        return new LocalCall<>("state.apply", Optional.empty(), Optional.of(args),
                new TypeToken<Map<String, Object>>() {
                });
    }
}
