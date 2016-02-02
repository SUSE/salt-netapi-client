package com.suse.salt.netapi.config;

import org.junit.Test;

import static com.suse.salt.netapi.config.ClientConfig.PROXY_PORT;
import static org.junit.Assert.assertEquals;

import com.suse.salt.netapi.config.ClientConfig;
import com.suse.salt.netapi.config.ClientConfig.Key;

/**
 * Configuration unit tests.
 */
public class ClientConfigTest {

    @Test
    public void testPutGetRemove() {
        ClientConfig config = new ClientConfig();
        Key<Integer> key = PROXY_PORT;

        assertEquals("New empty config should return defaultValue",
                key.defaultValue, config.get(key));

        Integer newValue = 123;
        config.put(key, newValue);
        assertEquals("Should return the new configured value", newValue, config.get(key));

        config.put(key, key.defaultValue);
        assertEquals("Should return the new configured value",
                key.defaultValue, config.get(key));

        config.put(key, newValue);
        assertEquals("Should return the new configured value", newValue, config.get(key));
        config.put(key, null);
        assertEquals("Should return the default value after putting in null",
                config.get(key), key.defaultValue);

        config.put(key, newValue);
        assertEquals("Should return the new configured value", newValue, config.get(key));
        config.remove(key);
        assertEquals("Should return the default value after removing the key",
                key.defaultValue, config.get(key));
    }
}
