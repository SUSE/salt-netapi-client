package com.suse.salt.netapi.calls.modules;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.suse.salt.netapi.calls.LocalCall;

import com.google.gson.reflect.TypeToken;
import com.suse.salt.netapi.parser.JsonParser;

/**
 * salt.modules.saltutil
 */
public class SaltUtil {

    public static LocalCall<List<String>> syncBeacons(
            Optional<Boolean> refresh, Optional<String> saltenv) {
        LinkedHashMap<String, Object> args = syncArgs(refresh, saltenv);
        return new LocalCall<>("saltutil.sync_beacons", Optional.empty(),
                Optional.of(args), new TypeToken<List<String>>() {
                });
    }

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

    public static LocalCall<Boolean> refreshPillar(
            Optional<Boolean> refresh, Optional<String> saltenv) {
        LinkedHashMap<String, Object> args = syncArgs(refresh, saltenv);
        return new LocalCall<>("saltutil.refresh_pillar", Optional.empty(),
                Optional.of(args), new TypeToken<Boolean>() {
                });
    }

    private static LinkedHashMap<String, Object> syncArgs(
            Optional<Boolean> refresh, Optional<String> saltenv) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        refresh.ifPresent(value -> args.put("refresh", value));
        saltenv.ifPresent(value -> args.put("saltenv", value));
        return args;
    }

    /**
     * Info about a running job on a minion
     */
    public static class RunningInfo {
        private String jid;
        private String fun;
        private int pid;
        private String target;
        @SerializedName("tgt_type")
        private String targetType;
        private String user;
        private Optional<JsonElement> metadata = Optional.empty();

        public <R> Optional<R> getMetadata(Class<R> type) {
            return metadata.map(json -> JsonParser.GSON.fromJson(json, type));
        }

        public <R> Optional<R> getMetadata(TypeToken<R> type) {
            return metadata.map(json -> JsonParser.GSON.fromJson(json, type.getType()));
        }

        public String getJid() {
            return jid;
        }

        public String getFun() {
            return fun;
        }

        public int getPid() {
            return pid;
        }

        public String getTarget() {
            return target;
        }

        public String getTargetType() {
            return targetType;
        }

        public String getUser() {
            return user;
        }
    }

    public static LocalCall<List<RunningInfo>> running() {
        return new LocalCall<>("saltutil.running",
                Optional.empty(), Optional.empty(), new TypeToken<List<RunningInfo>>() {});
    }

}
