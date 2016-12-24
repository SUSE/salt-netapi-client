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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.suse.salt.netapi.calls.LocalCall;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.datatypes.target.MinionList;
import com.suse.salt.netapi.errors.GenericSaltError;
import com.suse.salt.netapi.exception.SaltException;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.utils.ClientUtils;

/**
 * Locate module unit tests.
 */
public class StatusTest {

    private static final int MOCK_HTTP_PORT = 8888;
    
    static final String JSON_UPTIME_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/status/uptime.json"));
    
    private SaltClient client;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(MOCK_HTTP_PORT);

    @Before
    public void init() {
        URI uri = URI.create("http://localhost:" + MOCK_HTTP_PORT);
        client = new SaltClient(uri);
    }
    
    @Test
    public final void testUptime() throws SaltException {
        // First we get the call to use in the tests
        LocalCall<Map<String, Object>> call = Status.uptime();
        assertEquals("status.uptime", call.getPayload().get("fun"));

        // Test with an successful response
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_UPTIME_RESPONSE)));

        Map<String, Result<Map<String, Object>>> response = call.callSync(client,
                new MinionList("minion"));

        assertNotNull(response.get("minion"));
        Map<String, Object> minion = response.get("minion").result().get();
        assertNotNull(minion);
        assertEquals(6, minion.size());
        assertEquals(1.0, minion.get("users"));
        assertEquals(1.7906710E7, minion.get("seconds"));
        assertEquals(1.464613379E9, minion.get("since_t"));
        assertEquals(207.0, minion.get("days"));
        assertEquals("2016-05-30T13:02:59.145573", minion.get("since_iso"));
        assertEquals("6:5", minion.get("time"));
    }
}
