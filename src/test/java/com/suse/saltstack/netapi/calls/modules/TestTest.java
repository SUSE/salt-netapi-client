package com.suse.saltstack.netapi.calls.modules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.InputStream;

import com.google.gson.reflect.TypeToken;
import com.suse.saltstack.netapi.calls.modules.Test.VersionInformation;
import com.suse.saltstack.netapi.parser.JsonParser;

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
    }
}
