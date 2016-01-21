package com.suse.saltstack.netapi.calls.modules;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.suse.saltstack.netapi.client.SaltStackClient;
import com.suse.saltstack.netapi.datatypes.target.MinionList;
import com.suse.saltstack.netapi.exception.SaltStackException;
import com.suse.saltstack.netapi.utils.ClientUtils;

/**
 * SaltUtil unit tests.
 */
public class SaltUtilTest {

    private static final int MOCK_HTTP_PORT = 8888;

    static final String JSON_SYNCGRAINS_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/saltutil/syncgrains.json"));

    static final String JSON_SYNCMODULES_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/saltutil/syncmodules.json"));

    static final String JSON_SYNCALL_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/saltutil/syncall.json"));

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(MOCK_HTTP_PORT);

    private SaltStackClient client;

    @Before
    public void init() {
        URI uri = URI.create("http://localhost:" + Integer.toString(MOCK_HTTP_PORT));
        client = new SaltStackClient(uri);
    }

    @Test
    public void testSyncGrains() throws SaltStackException {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_SYNCGRAINS_RESPONSE)));

        Map<String, List<String>> response = SaltUtil.syncGrains(null, true)
                .callSync(client, new MinionList("minion1"));

        assertEquals(1, response.size());
        assertEquals("minion1", response.entrySet().iterator().next().getKey());
        assertEquals(0, response.entrySet().iterator().next().getValue().size());
    }

    @Test
    public void testSyncModules() throws SaltStackException {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_SYNCMODULES_RESPONSE)));

        Map<String, List<String>> response = SaltUtil.syncModules(null, true)
                .callSync(client, new MinionList("minion1"));

        assertEquals(1, response.size());
        assertEquals("minion1", response.entrySet().iterator().next().getKey());
        assertEquals(0, response.entrySet().iterator().next().getValue().size());
    }

    @Test
    public void testSyncAll() throws SaltStackException {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_SYNCALL_RESPONSE)));

        Map<String, Map<String, Object>> response = SaltUtil.syncAll(null, true)
                .callSync(client, new MinionList("minion1"));

        assertEquals(1, response.size());
        assertNotNull(response.get("minion1"));
        Map<String, Object> data = response.get("minion1");
        assertEquals(0, ((List<?>) data.get("beacons")).size());
        assertEquals(0, ((List<?>) data.get("grains")).size());
        assertEquals(0, ((List<?>) data.get("log_handlers")).size());
        assertEquals(0, ((List<?>) data.get("modules")).size());
        assertEquals(0, ((List<?>) data.get("output")).size());
        assertEquals(0, ((List<?>) data.get("proxymodules")).size());
        assertEquals(0, ((List<?>) data.get("renderers")).size());
        assertEquals(0, ((List<?>) data.get("returners")).size());
        assertEquals(0, ((List<?>) data.get("states")).size());
        assertEquals(0, ((List<?>) data.get("utils")).size());
    }
}
