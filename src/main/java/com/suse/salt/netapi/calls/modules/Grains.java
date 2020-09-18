package com.suse.salt.netapi.calls.modules;

import com.suse.salt.netapi.calls.LocalCall;

import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * salt.modules.grains
 */
public class Grains {

    private Grains() { }

    private static LocalCall<List<String>> LS =
            new LocalCall<>("grains.ls", Optional.empty(),
            Optional.empty(), new TypeToken<List<String>>(){});

    public static LocalCall<Map<String, Object>> items(boolean sanitize) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("sanitize", sanitize);
        return new LocalCall<>("grains.items", Optional.empty(), Optional.of(args),
                new TypeToken<Map<String, Object>>(){});
    }

    public static LocalCall<List<String>> ls() {
        return LS;
    }

    public static LocalCall<Map<String, Object>> item(boolean sanitize,
            String... items) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("sanitize", sanitize);
        return new LocalCall<>("grains.item", Optional.of(Arrays.asList(items)),
                Optional.of(args), new TypeToken<Map<String, Object>>(){});
    }

    public static LocalCall<Boolean> hasValue(String key) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("key", key);
        return new LocalCall<>("grains.has_value", Optional.empty(), Optional.of(args),
                new TypeToken<Boolean>(){});
    }

    public static LocalCall<Map<String, Object>> set(String key,  Optional<Map<String, Object>> extraArgs) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("key", key);
        extraArgs.ifPresent(args::putAll);
        return new LocalCall<>("grains.set", Optional.empty(), Optional.of(args),
                new TypeToken<Map<String, Object>>() { });
    }

    public static LocalCall<Map<String, Object>> setValue(String key, String value, Optional<Boolean> destructive) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("key", key);
        args.put("val", value);
        destructive.ifPresent(dest -> { args.put("destructive", dest); });
        return new LocalCall<>("grains.setval", Optional.empty(), Optional.of(args),
               new TypeToken<Map<String, Object>>() { });
    }

    public static LocalCall<Map<String, Object>> setValues(Map<String, Object> grains, Optional<Boolean> destructive) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("grains", grains);
        destructive.ifPresent(dest -> { args.put("destructive", dest); });
        return new LocalCall<>("grains.setvals", Optional.empty(), Optional.of(args),
                new TypeToken<Map<String, Object>>() { });
    }

    public static LocalCall<Map<String, Object>> append(String key, String value, Optional<Boolean> convert,
                Optional<Boolean> delimiter) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("key", key);
        args.put("val", value);
        convert.ifPresent(cnvrt -> { args.put("convert", cnvrt); });
        delimiter.ifPresent(dlmtr -> { args.put("delimiter", dlmtr); });
        return new LocalCall<>("grains.append", Optional.empty(), Optional.of(args),
                new TypeToken<Map<String, Object>>() { });
    }

    public static LocalCall<Map<String, Object>> deleteKey(String key) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("key", key);
        return new LocalCall<>("grains.delkey", Optional.empty(), Optional.of(args),
                new TypeToken<Map<String, Object>>() { });
    }

    public static LocalCall<Map<String, Object>> deleteValue(String key, Optional<Boolean> destructive) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("key", key);
        destructive.ifPresent(dest -> { args.put("destructive", dest); });
        return new LocalCall<>("grains.delval", Optional.empty(), Optional.of(args),
                new TypeToken<Map<String, Object>>() { });
    }

    public static LocalCall<Map<String, Object>> remove(String key, String value, Optional<Boolean> delimiter) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("key", key);
        args.put("val", value);
        delimiter.ifPresent(dlmtr -> { args.put("delimiter", dlmtr); });
        return new LocalCall<>("grains.remove", Optional.empty(), Optional.of(args),
                new TypeToken<Map<String, Object>>() { });
    }

    public static <T> LocalCall<T> item(boolean sanitize,
                                         TypeToken<T> type, String... items) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("sanitize", sanitize);
        return new LocalCall<T>("grains.item", Optional.of(Arrays.asList(items)),
                Optional.of(args), type);
    }
}
