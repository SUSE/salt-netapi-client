package com.suse.saltstack.netapi.calls.modules;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.gson.reflect.TypeToken;
import com.suse.saltstack.netapi.calls.LocalCall;

/**
 * salt.modules.saltutil
 */
public class SaltUtil {

    public static LocalCall<List<String>> syncGrains(String saltenv, boolean refresh) {
        LinkedHashMap<String, Object> args = syncArgs(saltenv, refresh);
        return new LocalCall<>("saltutil.sync_grains", Optional.empty(),
                Optional.of(args), new TypeToken<List<String>>() {
                });
    }

    public static LocalCall<List<String>> syncModules(String saltenv, boolean refresh) {
        LinkedHashMap<String, Object> args = syncArgs(saltenv, refresh);
        return new LocalCall<>("saltutil.sync_modules", Optional.empty(),
                Optional.of(args), new TypeToken<List<String>>() {
                });
    }

    public static LocalCall<Map<String, Object>> syncAll(String saltenv, boolean refresh) {
        LinkedHashMap<String, Object> args = syncArgs(saltenv, refresh);
        return new LocalCall<>("saltutil.sync_all", Optional.empty(),
                Optional.of(args), new TypeToken<Map<String, Object>>() {
                });
    }

    private static LinkedHashMap<String, Object> syncArgs(String saltenv, boolean refresh) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        if (saltenv != null) {
            args.put("saltenv", saltenv);
        }
        args.put("refresh", refresh);
        return args;
    }

}
