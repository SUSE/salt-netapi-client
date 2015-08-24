package com.suse.saltstack.netapi.calls.modules;

import com.google.gson.reflect.TypeToken;
import com.suse.saltstack.netapi.calls.LocalCall;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * salt.modules.event
 */
public class Event {

    private Event() { }

    public static LocalCall<Boolean> fire(Map<String, Object> data,
            String tag) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("data", data);
        args.put("tag", tag);
        return new LocalCall<>("event.fire", Optional.empty(), Optional.of(args),
                new TypeToken<Boolean>() {});
    }

    public static LocalCall<Boolean> fireMaster(Map<String, Object> data,
            String tag) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("data", data);
        args.put("tag", tag);
        return new LocalCall<>("event.fire_master", Optional.empty(), Optional.of(args),
                new TypeToken<Boolean>() {});
    }
}
