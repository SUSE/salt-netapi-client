package com.suse.salt.netapi.calls.modules;

import com.suse.salt.netapi.calls.LocalCall;

import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * salt.modules.match
 */
public class Match {

    /**
     * Return True if the minion ID matches the given compound target
     * @param tgt
     * @param minionId
     * @return the {@link LocalCall} object to make the call
     */
    public static LocalCall<Boolean> compound(String tgt, Optional<String> minionId) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("tgt", tgt);
        minionId.ifPresent(id -> args.put("minion_id", id));
        return new LocalCall<>("match.compound", Optional.empty(),
                Optional.of(args), new TypeToken<Boolean>(){});
    }

    /**
     * Return True if it matches the given compound target
     * @param tgt
     * @return the {@link LocalCall} object to make the call
     */
    public static LocalCall<Boolean> compound(String tgt) {
        return compound(tgt, Optional.empty());
    }

    /**
     * Return True if the minion ID matches the given glob target
     * @param tgt
     * @param minionId
     * @return the {@link LocalCall} object to make the call
     */
    public static LocalCall<Boolean> glob(String tgt, Optional<String> minionId) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("tgt", tgt);
        minionId.ifPresent(id -> args.put("minion_id", id));
        return new LocalCall<>("match.glob", Optional.empty(),
                Optional.of(args), new TypeToken<Boolean>(){});
    }

    /**
     * Return True if it matches the given glob target
     * @param tgt
     * @return the {@link LocalCall} object to make the call
     */
    public static LocalCall<Boolean> glob(String tgt) {
        return glob(tgt, Optional.empty());
    }

    /**
     * Return True if the minion matches the given grain target.
     * The delimiter argument can be used to specify a different delimiter.
     * @param tgt
     * @param delimiter
     * @return the {@link LocalCall} object to make the call
     */
    public static LocalCall<Boolean> grain(String tgt, Optional<String> delimiter) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("tgt", tgt);
        delimiter.ifPresent(d -> args.put("delimiter", d));
        return new LocalCall<>("match.grain", Optional.empty(),
                Optional.of(args), new TypeToken<Boolean>(){});
    }

    /**
     * Return True if the minion matches the given pillar target.
     * The delimiter argument can be used to specify a different delimiter.
     * @param tgt
     * @param delimiter
     * @return the {@link LocalCall} object to make the call
     */
    public static LocalCall<Boolean> pillar(String tgt, Optional<String> delimiter) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("tgt", tgt);
        delimiter.ifPresent(d -> args.put("delimiter", d));
        return new LocalCall<>("match.pillar", Optional.empty(),
                Optional.of(args), new TypeToken<Boolean>(){});
    }

    /**
     * Return True if the minion matches the given data target
     * @param tgt
     * @return the {@link LocalCall} object to make the call
     */
    public static LocalCall<Boolean> data(String tgt) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("tgt", tgt);
        return new LocalCall<>("match.data", Optional.empty(),
                Optional.of(args), new TypeToken<Boolean>(){});
    }

    /**
     * Return True if the minion ID matches the given list target
     * @param tgt
     * @param minionId
     * @return the {@link LocalCall} object to make the call
     */
    public static LocalCall<Boolean> list(List<String> tgt, Optional<String> minionId) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("tgt", tgt.stream().collect(Collectors.joining(",")));
        minionId.ifPresent(id -> args.put("minion_id", id));
        return new LocalCall<>("match.list", Optional.empty(),
                Optional.of(args), new TypeToken<Boolean>(){});
    }

    /**
     * Return True if it matches the given list target
     * @param tgt
     * @return the {@link LocalCall} object to make the call
     */
    public static LocalCall<Boolean> list(String... tgt) {
        return list(Arrays.asList(tgt), Optional.empty());
    }
}
