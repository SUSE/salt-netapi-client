package com.suse.saltstack.netapi.calls.modules;

import com.google.gson.reflect.TypeToken;
import com.suse.saltstack.netapi.calls.LocalCall;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * salt.modules.beacons
 */
public class Beacon {

    private static final LocalCall<Result> DISABLE = new LocalCall<>("beacons.disable",
            Optional.empty(), Optional.empty(),
            new TypeToken<Result>() {});

    private static final LocalCall<Result> ENABLE = new LocalCall<>("beacons.enable",
            Optional.empty(), Optional.empty(),
            new TypeToken<Result>() {});

    private static final LocalCall<Result> SAVE = new LocalCall<>("beacons.save",
            Optional.empty(), Optional.empty(),
            new TypeToken<Result>() {});

    /**
     * Status result of many beacon functions
     */
    public static class Result {
        private final String comment;
        private final boolean result;

        public Result(String comment, boolean result) {
            this.comment = comment;
            this.result = result;
        }

        public String getComment() {
            return comment;
        }

        public boolean getResult() {
            return result;
        }
    }

    public static LocalCall<Result> add(String name, Map<String, Object> data) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("name", name);
        args.put("beacon_data", data);
        return new LocalCall<>("beacons.add", Optional.empty(), Optional.of(args),
                new TypeToken<Result>() {});
    }

    public static LocalCall<Result> modify(String name, Map<String, Object> data) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("name", name);
        args.put("beacon_data", data);
        return new LocalCall<>("beacons.modify", Optional.empty(), Optional.of(args),
                new TypeToken<Result>() {});
    }

    public static LocalCall<Result> delete(String name) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("name", name);
        return new LocalCall<>("beacons.delete", Optional.empty(), Optional.of(args),
                new TypeToken<Result>() {});
    }

    public static LocalCall<Result> disable() {
        return DISABLE;
    }

    public static LocalCall<Result> disableBeacon(String name) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("name", name);
        return new LocalCall<>("beacons.disable_beacon", Optional.empty(),
                Optional.of(args), new TypeToken<Result>() {});
    }

    public static LocalCall<Result> enable() {
        return ENABLE;
    }

    public static LocalCall<Result> enableBeacon(String name) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("name", name);
        return new LocalCall<>("beacons.enable_beacon", Optional.empty(), Optional.of(args),
                new TypeToken<Result>() {});
    }

    public static LocalCall<Map<String, Object>> list() {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("return_yaml", false);
        return new LocalCall<>("beacons.list", Optional.empty(), Optional.of(args),
                new TypeToken<Map<String, Object>>() {});
    }

    public static LocalCall<Result> save() {
        return SAVE;
    }
}
