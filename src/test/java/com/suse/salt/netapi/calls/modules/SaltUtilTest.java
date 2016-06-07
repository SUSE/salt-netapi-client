package com.suse.salt.netapi.calls.modules;

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
import java.util.Optional;

import com.suse.salt.netapi.results.Result;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.datatypes.target.MinionList;
import com.suse.salt.netapi.exception.SaltException;
import com.suse.salt.netapi.utils.ClientUtils;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

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

    static final String JSON_REFRESHPILLAR_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/saltutil/refreshpillar.json"));

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(MOCK_HTTP_PORT);

    private SaltClient client;

    @Before
    public void init() {
        URI uri = URI.create("http://localhost:" + Integer.toString(MOCK_HTTP_PORT));
        client = new SaltClient(uri);
    }

    @Test
    public void testSyncGrains() throws SaltException {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_SYNCGRAINS_RESPONSE)));

        Map<String, Result<List<String>>> response = SaltUtil
                .syncGrains(Optional.of(true), Optional.empty())
                .callSync(client, new MinionList("minion1"));

        assertEquals(1, response.size());
        assertEquals("minion1", response.entrySet().iterator().next().getKey());
        assertEquals(0, response.entrySet().iterator().next()
                .getValue().result().get().size());
    }

    @Test
    public void testSyncModules() throws SaltException {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_SYNCMODULES_RESPONSE)));

        Map<String, Result<List<String>>> response = SaltUtil
                .syncModules(Optional.of(true), Optional.empty())
                .callSync(client, new MinionList("minion1"));

        assertEquals(1, response.size());
        assertEquals("minion1", response.entrySet().iterator().next().getKey());
        assertEquals(0, response.entrySet().iterator().next()
                .getValue().result().get().size());
    }

    @Test
    public void testSyncAll() throws SaltException {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_SYNCALL_RESPONSE)));

        Map<String, Result<Map<String, Object>>> response = SaltUtil
                .syncAll(Optional.of(true), Optional.empty())
                .callSync(client, new MinionList("minion1"));

        assertEquals(1, response.size());
        assertNotNull(response.get("minion1"));
        Map<String, Object> data = response.get("minion1").result().get();
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

    @Test
    public void testRefreshPillar() throws SaltException {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_REFRESHPILLAR_RESPONSE)));

        Map<String, Result<Boolean>> response = SaltUtil
                .refreshPillar(Optional.of(true), Optional.empty())
                .callSync(client, new MinionList("minion1"));
        assertEquals(1, response.size());
        assertNotNull(response.get("minion1"));
        Boolean data = response.get("minion1").result().get();
        assertEquals(true, data);
    }
}
