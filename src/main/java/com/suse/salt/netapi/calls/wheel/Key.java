package com.suse.salt.netapi.calls.wheel;

import com.suse.salt.netapi.calls.WheelCall;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * salt.wheel.key
 */
public class Key {

    /**
     * Salt keys information.
     *
     * @param <T> type of data queried for each key state
     */
    public static abstract class Keys<T> {

        protected T local;

        protected T minions;

        @SerializedName("minions_pre")
        protected T unacceptedMinions;

        @SerializedName("minions_rejected")
        protected T rejectedMinions;

        @SerializedName("minions_denied")
        protected T deniedMinions;

        public T getLocal() {
            return local;
        }

        public T getMinions() {
            return minions;
        }

        public T getUnacceptedMinions() {
            return unacceptedMinions;
        }

        public T getRejectedMinions() {
            return rejectedMinions;
        }

        public T getDeniedMinions() {
            return deniedMinions;
        }
    }

    /**
     * Salt keys as returned by "key.list_all".
     */
    public static class Names extends Keys<List<String>> {

        public Names() {
            this.local = new LinkedList<>();
            this.minions = new LinkedList<>();
            this.unacceptedMinions = new LinkedList<>();
            this.rejectedMinions = new LinkedList<>();
            this.deniedMinions = new LinkedList<>();
        }
    }

    /**
     * Matching key fingerprints as returned by "key.finger".
     */
    public static class Fingerprints extends Keys<Map<String, String>> {

        public Fingerprints() {
            this.local = new HashMap<>();
            this.minions = new HashMap<>();
            this.unacceptedMinions = new HashMap<>();
            this.rejectedMinions = new HashMap<>();
            this.deniedMinions = new HashMap<>();
        }
    }

    /**
     * A key pair as returned by "key.gen" or "key.gen_accept".
     */
    public static class Pair {

        private Optional<String> pub = Optional.empty();
        private Optional<String> priv = Optional.empty();

        public Optional<String> getPub() {
            return pub;
        }

        public Optional<String> getPriv() {
            return priv;
        }
    }

    private Key() {
    }

    private static final WheelCall<Names> LIST_ALL =
            new WheelCall<>("key.list_all", Optional.empty(), new TypeToken<Names>(){});

    public static WheelCall<Fingerprints> finger(String match) {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("match", match);
        return new WheelCall<>("key.finger", Optional.of(args),
                new TypeToken<Fingerprints>(){});
    }

    public static WheelCall<Pair> gen(String id) {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("id_", id);
        return new WheelCall<>("key.gen", Optional.of(args), new TypeToken<Pair>(){});
    }

    public static WheelCall<Pair> genAccept(String id) {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("id_", id);
        return new WheelCall<>("key.gen_accept", Optional.of(args),
                new TypeToken<Pair>(){});
    }

    public static WheelCall<Object> accept(String match) {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("match", match);
        return new WheelCall<>("key.accept", Optional.of(args), new TypeToken<Object>(){});
    }

    public static WheelCall<Object> delete(String match) {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("match", match);
        return new WheelCall<>("key.delete", Optional.of(args), new TypeToken<Object>(){});
    }

    public static WheelCall<Object> reject(String match) {
        Map<String, Object> args = new LinkedHashMap<>();
        args.put("match", match);
        return new WheelCall<>("key.reject", Optional.of(args), new TypeToken<Object>(){});
    }

    public static WheelCall<Names> listAll() {
        return LIST_ALL;
    }
}
