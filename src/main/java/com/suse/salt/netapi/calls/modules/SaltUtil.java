package com.suse.salt.netapi.calls.modules;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.suse.salt.netapi.calls.LocalCall;

import com.google.gson.reflect.TypeToken;

/**
 * salt.modules.saltutil
 */
public class SaltUtil {

    public static LocalCall<List<String>> syncGrains(
            Optional<Boolean> refresh, Optional<String> saltenv) {
        LinkedHashMap<String, Object> args = syncArgs(refresh, saltenv);
        return new LocalCall<>("saltutil.sync_grains", Optional.empty(),
                Optional.of(args), new TypeToken<List<String>>() {
                });
    }

    public static LocalCall<List<String>> syncModules(
            Optional<Boolean> refresh, Optional<String> saltenv) {
        LinkedHashMap<String, Object> args = syncArgs(refresh, saltenv);
        return new LocalCall<>("saltutil.sync_modules", Optional.empty(),
                Optional.of(args), new TypeToken<List<String>>() {
                });
    }

    public static LocalCall<Map<String, Object>> syncAll(
            Optional<Boolean> refresh, Optional<String> saltenv) {
        LinkedHashMap<String, Object> args = syncArgs(refresh, saltenv);
        return new LocalCall<>("saltutil.sync_all", Optional.empty(),
                Optional.of(args), new TypeToken<Map<String, Object>>() {
                });
    }

    private static LinkedHashMap<String, Object> syncArgs(
            Optional<Boolean> refresh, Optional<String> saltenv) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        refresh.ifPresent(value -> args.put("refresh", value));
        saltenv.ifPresent(value -> args.put("saltenv", value));
        return args;
    }

}
