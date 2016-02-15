package com.suse.salt.netapi.calls.modules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.suse.salt.netapi.calls.modules.Test.VersionInformation;
import com.suse.salt.netapi.parser.JsonParser;

import com.google.gson.reflect.TypeToken;

/**
 * Test unit tests.
 */
public class TestTest {

    @org.junit.Test
    public void testTestPing() {
        TypeToken<Boolean> type = Test.ping().getReturnType();
        InputStream is = this.getClass()
                .getResourceAsStream("/modules/test/ping.json");

        JsonParser<Boolean> parser = new JsonParser<>(type);
        Boolean parsed = parser.parse(is);
        assertEquals(true, parsed);
    }

    @org.junit.Test
    public void testVersionsInformation() {
        TypeToken<VersionInformation> type = Test.versionsInformation()
                .getReturnType();
        InputStream is = this.getClass()
                .getResourceAsStream("/modules/test/versions_information.json");
        JsonParser<VersionInformation> parser = new JsonParser<>(type);
        VersionInformation parsed = parser.parse(is);
        assertEquals("2015.8.0-574-g99384bc", parsed.getSalt().get("Salt"));
        assertEquals("3.13.0-65-generic", parsed.getSystem().get("release"));
        assertEquals("Ubuntu 14.04 trusty", parsed.getSystem().get("dist"));
        assertEquals("Ubuntu 14.04 trusty", parsed.getSystem().get("system"));
        assertNull(parsed.getDependencies().get("cherrypy"));
        assertEquals("2.7.6 (default, Jun 22 2015, 17:58:13)",
                parsed.getDependencies().get("Python"));
        assertEquals(25, parsed.getDependencies().size());
    }

    @org.junit.Test
    public void testTestRandStr() {
        TypeToken<Map<String, List<Map<String, String>>>> type = Test.randStr().getReturnType();
        InputStream is = this.getClass()
                .getResourceAsStream("/modules/test/rand_str.json");

        JsonParser<Map<String, List<Map<String, String>>>> parser = new JsonParser<>(type);
        Map<String, List<Map<String, String>>> parsed = parser.parse(is);
        assertEquals(true, parsed.containsKey("return"));
        assertEquals(true, parsed.get("return").get(0).containsKey("minion1"));
        assertEquals("6960283c0a8f1f2361ecdc3f9513c1d3", parsed.get("return").get(0).get("minion1"));
    }
}
