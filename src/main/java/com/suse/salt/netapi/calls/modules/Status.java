package com.suse.salt.netapi.calls.modules;

import com.suse.salt.netapi.calls.LocalCall;

import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * salt.modules.status
 */
public class Status {

    private Status() { }

    public static LocalCall<Map<String, Object>> allstatus() {
        return new LocalCall<>("status.all_status", Optional.empty(), Optional.empty(),
                new TypeToken<Map<String, Object>>(){});
    }

    public static LocalCall<Map<String, Object>> cpuinfo() {
        return new LocalCall<>("status.cpuinfo", Optional.empty(), Optional.empty(),
                new TypeToken<Map<String, Object>>(){});
    }

    public static LocalCall<Map<String, Object>> cpustats() {
        return new LocalCall<>("status.cpustats", Optional.empty(), Optional.empty(),
                new TypeToken<Map<String, Object>>(){});
    }

    public static LocalCall<Map<String, Object>> custom() {
        return new LocalCall<>("status.custom", Optional.empty(), Optional.empty(),
                new TypeToken<Map<String, Object>>(){});
    }

    public static LocalCall<Map<String, Map<String, Object>>> diskstats() {
        return new LocalCall<>("status.diskstats", Optional.empty(), Optional.empty(),
                new TypeToken<Map<String, Map<String, Object>>>(){});
    }

    public static LocalCall<Map<String, Map<String, Long>>> diskusage(
            String... pathsOrFSTypes) {
        List<String> args = Arrays.asList(pathsOrFSTypes);
        return new LocalCall<>("status.diskusage", Optional.of(args), Optional.empty(),
                new TypeToken<Map<String, Map<String, Long>>>(){});
    }

    public static LocalCall<Map<String, Double>> loadavg() {
        return new LocalCall<>("status.loadavg", Optional.empty(), Optional.empty(),
                new TypeToken<Map<String, Double>>(){});
    }

    public static LocalCall<Map<String, Map<String, Object>>> meminfo() {
        return new LocalCall<>("status.meminfo", Optional.empty(), Optional.empty(),
                new TypeToken<Map<String, Map<String, Object>>>(){});
    }

    public static LocalCall<Map<String, Map<String, Object>>> netdev() {
        return new LocalCall<>("status.netdev", Optional.empty(), Optional.empty(),
                new TypeToken<Map<String, Map<String, Object>>>(){});
    }

    public static LocalCall<Map<String, Map<String, Long>>> netstats() {
        return new LocalCall<>("status.netstats", Optional.empty(), Optional.empty(),
                new TypeToken<Map<String, Map<String, Long>>>(){});
    }

    public static LocalCall<Integer> nproc() {
        return new LocalCall<>("status.nproc", Optional.empty(), Optional.empty(),
                new TypeToken<Integer>(){});
    }

    public static LocalCall<String> pid(String signature) {
        List<String> args = Collections.singletonList(signature);
        return new LocalCall<>("status.pid", Optional.of(args), Optional.empty(),
                new TypeToken<String>(){});
    }

    public static LocalCall<Map<Integer, Map<String, String>>> procs() {
        return new LocalCall<>("status.procs", Optional.empty(), Optional.empty(),
                new TypeToken<Map<Integer, Map<String, String>>>(){});
    }

    public static LocalCall<Float> uptime() {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        // This requires salt-master version to be >= 2015.8.4
        args.put("human_readable", false);
        return new LocalCall<>("status.uptime", Optional.empty(), Optional.of(args),
                new TypeToken<Float>(){});
    }

    public static LocalCall<String> version() {
        return new LocalCall<>("status.version", Optional.empty(), Optional.empty(),
                new TypeToken<String>(){});
    }

    public static LocalCall<Map<String, Long>> vmstats() {
        return new LocalCall<>("status.vmstats", Optional.empty(), Optional.empty(),
                new TypeToken<Map<String, Long>>(){});
    }

    public static LocalCall<List<Map<String, String>>> w() {
        return new LocalCall<>("status.w", Optional.empty(), Optional.empty(),
                new TypeToken<List<Map<String, String>>>(){});
    }
}
