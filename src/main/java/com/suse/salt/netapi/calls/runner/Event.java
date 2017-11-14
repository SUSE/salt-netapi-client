package com.suse.salt.netapi.calls.runner;

import com.suse.salt.netapi.calls.RunnerCall;

import com.google.gson.reflect.TypeToken;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * salt.runners.event
 */
public class Event {

    private Event() { }

    public static RunnerCall<Boolean> send(String tag, Optional<Map<String, Object>> data) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("tag", tag);
        data.ifPresent(d -> args.put("data", d));
        return new RunnerCall<>("event.send", Optional.of(args),
                new TypeToken<Boolean>(){});
    }
}
