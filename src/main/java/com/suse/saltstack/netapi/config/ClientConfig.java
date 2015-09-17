package com.suse.saltstack.netapi.config;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * A statically typed key/value store for the Saltstack client configuration.
 */
public class ClientConfig {

    public static final Key<URI> URL = new Key<>(URI.create("http://localhost:8000"));
    public static final Key<String> TOKEN = new Key<>();

    /**
     * Timeout in milliseconds until a connection is established.
     * A timeout of zero is interpreted as an infinite timeout.
     * A negative value is interpreted as undefined (system default).
     * Default value is 10000ms (10s)
     */
    public static final Key<Integer> CONNECT_TIMEOUT = new Key<>(10000);

    /**
     * Timeout in milliseconds for waiting for data.
     * A timeout of zero is interpreted as an infinite timeout.
     * A negative value is interpreted as undefined (system default).
     * Default value is 10000ms (10s)
     */
    public static final Key<Integer> SOCKET_TIMEOUT = new Key<>(10000);

    // Proxy settings
    public static final Key<String> PROXY_HOSTNAME = new Key<>();
    public static final Key<Integer> PROXY_PORT = new Key<>(3128);
    public static final Key<String> PROXY_USERNAME = new Key<>();
    public static final Key<String> PROXY_PASSWORD = new Key<>();

    /**
     * Maximum websocket message length in characters. The default value corresponds to
     * 10 MB, assuming that one character takes up 2 bytes. This limit can be disabled
     * by setting a value less than or equal to 0.
     */
    public static final Key<Integer> WEBSOCKET_MAX_MESSAGE_LENGTH = new Key<>(0x500000);

    /**
     * A key to use with {@link ClientConfig}.
     * @param <T> The type of the value associated with this key.
     */
    static class Key<T> {

        /** The default value of this key */
        public final T defaultValue;

        /**
         * Creates a new Key with the default value null.
         */
        public Key() {
            this(null);
        }

        /**
         * Creates a new key with the specified default value.
         *
         * @param defaultValue Default value for this key.
         */
        public Key(T defaultValue) {
            this.defaultValue = defaultValue;
        }

    }

    private final Map<Key<?>, Object> store = new HashMap<>();

    /**
     *  Sets the config for a key to the specified value.
     *
     * @param key The configuration key to set.
     * @param value The value to associate with the key.
     * @param <T> The type of the value associated with the key.
     */
    public <T> void put(Key<T> key, T value) {
        if (value == null || value.equals(key.defaultValue)) {
            remove(key);
        } else {
            store.put(key, value);
        }
    }

    /**
     *  Removes the value for the specified key. This is equivalent to setting
     *  the value to the default value of the key.
     *
     * @param key The configuration key to remove.
     * @param <T> The type of the value associated with the key.
     */
    public <T> void remove(Key<T> key) {
        store.remove(key);
    }

    /**
     * Returns the configured value for the given key. If the key is not explicitly set.
     * The default value is for that key is returned.
     *
     * @param key The configuration key.
     * @param <T> The type of the value associated with the key.
     * @return The current configured value for the key or the default value if not
     * configured.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Key<T> key) {
        Object value = store.get(key);
        return value != null ? (T) value : key.defaultValue;
    }
}
