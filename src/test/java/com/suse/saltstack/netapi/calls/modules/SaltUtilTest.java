package com.suse.saltstack.netapi.calls.modules;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.Assert.assertEquals;

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

        Map<String, List<String>> response = SaltUtil.syncGrains().callSync(client, new MinionList("localhost"));

        assertEquals(response.size(), 1);
        assertEquals(response.entrySet().iterator().next().getKey(), "localhost");
        assertEquals(response.entrySet().iterator().next().getValue().size(), 0);
    }

}
