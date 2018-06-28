package com.suse.salt.netapi.calls.modules;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.suse.salt.netapi.calls.LocalCall;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.datatypes.target.MinionList;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.utils.ClientUtils;

/**
 * Minion module unit tests.
 */
public class MinionTest {

    private static final int MOCK_HTTP_PORT = 8888;

    static final String JSON_LIST_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/minion/list.json"));

    static final String JSON_KILL_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/minion/kill.json"));

    static final String JSON_RESTART_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/minion/restart.json"));

    private SaltClient client;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(MOCK_HTTP_PORT);

    @Before
    public void init() {
        URI uri = URI.create("http://localhost:" + MOCK_HTTP_PORT);
        client = new SaltClient(uri);
    }

    @Test
    public final void testList() {
        // First we get the call to use in the tests
        LocalCall<Map<String, Set<String>>> call = Minion.list();
        assertEquals("minion.list", call.getPayload().get("fun"));

        // Test with an successful response
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_LIST_RESPONSE)));

        Map<String, Result<Map<String, Set<String>>>> response =
                call.callSync(client, new MinionList("master")).toCompletableFuture().join();

        assertNotNull(response.get("master"));

        Map<String, Set<String>> master = response.get("master").result().get();
        assertNotNull(master);

        assertEquals(4, master.size());

        Set<String> minions = master.get("minions");
        assertEquals(3, minions.size());
        String error = "minion list error";
        assertTrue(error, minions.contains("master"));
        assertTrue(error, minions.contains("minion1"));
        assertTrue(error, minions.contains("minion2"));

        Set<String> minions_rejected = master.get("minions_rejected");
        assertEquals(2, minions_rejected.size());
        assertTrue(error, minions_rejected.contains("minionrejected1"));
        assertTrue(error, minions_rejected.contains("minionrejected2"));

        Set<String> minions_denied = master.get("minions_denied");
        assertEquals(2, minions_denied.size());
        assertTrue(error, minions_denied.contains("miniondenied1"));
        assertTrue(error, minions_denied.contains("miniondenied2"));

        Set<String> minions_pre = master.get("minions_pre");
        assertEquals(2, minions_pre.size());
        assertTrue(error, minions_pre.contains("minionpre1"));
        assertTrue(error, minions_pre.contains("minionpre2"));
    }

    @Test
    public final void testKill() {
        // First we get the call to use in the tests
        LocalCall<Map<String, Object>> call = Minion.kill();
        assertEquals("minion.kill", call.getPayload().get("fun"));

        // Test with an successful response
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_KILL_RESPONSE)));

        Map<String, Result<Map<String, Object>>> response =
                call.callSync(client, new MinionList("minion")).toCompletableFuture().join();

        assertNotNull(response.get("minion"));

        Map<String, Object> minion = response.get("minion").result().get();
        assertNotNull(minion);

        assertEquals(2, minion.size());
        assertEquals(10417.0, minion.get("killed"));
        assertEquals(0.0, minion.get("retcode"));
    }

    @Test
    public final void testRestart() {
        // First we get the call to use in the tests
        LocalCall<Map<String, Object>> call = Minion.restart();
        assertEquals("minion.restart", call.getPayload().get("fun"));

        // Test with an successful response
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_RESTART_RESPONSE)));

        Map<String, Result<Map<String, Object>>> response =
                call.callSync(client, new MinionList("minion")).toCompletableFuture().join();

        assertNotNull(response.get("minion"));

        Map<String, Object> minion = response.get("minion").result().get();
        assertNotNull(minion);

        assertEquals(4, minion.size());
        assertEquals(10733.0, minion.get("killed"));
        assertEquals(0.0, minion.get("retcode"));
    }
}
