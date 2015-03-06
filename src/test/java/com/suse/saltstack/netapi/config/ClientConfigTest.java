package com.suse.saltstack.netapi.config;

import static com.suse.saltstack.netapi.config.ClientConfig.*;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ClientConfigTest {

    @Test
    public void testPutGetRemove() {
        ClientConfig config = new ClientConfig();
        Key<Integer> key = PROXY_PORT;

        assertEquals("New empty config should return defaultValue", config.get(key), key.defaultValue);

        Integer newValue = 123;
        config.put(key, newValue);
        assertEquals("Should return the new configured value", config.get(key), newValue);

        config.put(key, key.defaultValue);
        assertEquals("Should return the new configured value", config.get(key), key.defaultValue);

        config.put(key, newValue);
        assertEquals("Should return the new configured value", config.get(key), newValue);
        config.put(key, null);
        assertEquals("Should return the default value after putting in null", config.get(key), key.defaultValue);

        config.put(key, newValue);
        assertEquals("Should return the new configured value", config.get(key), newValue);
        config.remove(key);
        assertEquals("Should return the default value after removing the key", config.get(key), key.defaultValue);
    }
}
