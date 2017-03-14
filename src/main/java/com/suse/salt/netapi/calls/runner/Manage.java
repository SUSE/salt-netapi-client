package com.suse.salt.netapi.calls.runner;

import com.suse.salt.netapi.calls.RunnerCall;

import com.google.gson.reflect.TypeToken;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

/**
 * salt.runners.manage
 */
public class Manage {

    private Manage() { }

    public static RunnerCall<List<String>> down(boolean removekeys) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("removekeys", removekeys);
        return new RunnerCall<>("manage.down", Optional.of(args),
                new TypeToken<List<String>>(){});
    }

    public static RunnerCall<List<String>> up() {
        return new RunnerCall<>("manage.up", Optional.empty(),
                new TypeToken<List<String>>(){});
    }

    public static RunnerCall<List<String>> up(Optional<Integer> timeout,
            Optional<Integer> gatherJobTimeout) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        timeout.ifPresent(value -> args.put("timeout", value));
        gatherJobTimeout.ifPresent(value -> args.put("gather_job_timeout", value));
        return new RunnerCall<>("manage.up", Optional.of(args),
                new TypeToken<List<String>>(){});
    }

    public static RunnerCall<List<String>> present(Optional<String> subset,
            Optional<Boolean> showIpv4) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        subset.ifPresent(value -> args.put("subset", value));
        showIpv4.ifPresent(value -> args.put("show_ipv4", value));
        return new RunnerCall<>("manage.present", Optional.of(args),
                new TypeToken<List<String>>(){});
    }

    public static RunnerCall<List<String>> present() {
        return present(Optional.empty(), Optional.empty());
    }
}
