package com.suse.salt.netapi.calls.modules;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.JsonNull;
import com.suse.salt.netapi.calls.LocalCall;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.datatypes.target.MinionList;
import com.suse.salt.netapi.exception.SaltException;
import com.suse.salt.netapi.results.GenericSaltError;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.utils.ClientUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Locate module unit tests.
 */
public class LocateTest {

    private static final int MOCK_HTTP_PORT = 8888;

    private static final String JSON_NULL_RESPONSE = ClientUtils.streamToString(
            FileTest.class.getResourceAsStream(
            "/modules/locate/null_response.json"));

    private static final String JSON_VERSION_OK_RESPONSE = ClientUtils.streamToString(
            FileTest.class.getResourceAsStream(
            "/modules/locate/version_ok_response.json"));

    private static final String JSON_UPDATEDB_OK_RESPONSE = ClientUtils.streamToString(
            FileTest.class.getResourceAsStream(
            "/modules/locate/updatedb_ok_response.json"));

    private static final String JSON_UPDATEDB_ERROR_RESPONSE = ClientUtils.streamToString(
            FileTest.class.getResourceAsStream(
            "/modules/locate/updatedb_error_response.json"));

    private static final String JSON_STATS_OK_RESPONSE = ClientUtils.streamToString(
            FileTest.class.getResourceAsStream(
            "/modules/locate/stats_ok_response.json"));

    private static final String JSON_LOCATE_OK_RESPONSE = ClientUtils.streamToString(
            FileTest.class.getResourceAsStream(
            "/modules/locate/locate_ok_response.json"));

    private static final String JSON_LOCATE_NOINPUT_RESPONSE = ClientUtils.streamToString(
            FileTest.class.getResourceAsStream(
            "/modules/locate/locate_noinput_response.json"));

    private SaltClient client;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(MOCK_HTTP_PORT);

    @Before
    public void init() {
        URI uri = URI.create("http://localhost:" + MOCK_HTTP_PORT);
        client = new SaltClient(uri);
    }

    @Test
    public final void testVersion() throws SaltException {
        // First we get the call to use in the tests
        LocalCall<List<String>> call = Locate.version();
        assertEquals("locate.version", call.getPayload().get("fun"));

        // Test for successful responses
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_VERSION_OK_RESPONSE)));

        Map<String, Result<List<String>>> response = call.callSync(client,
                new MinionList("minion1"));

        assertNotNull(response.get("minion1"));
        assertNotNull(response.get("minion1").result().get());
        assertEquals(5, response.get("minion1").result().get().size());
        assertEquals("mlocate 0.26", response.get("minion1").result().get().get(0));

        // Test for null responses
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_NULL_RESPONSE)));


        response = call.callSync(client, new MinionList("minion1"));

        assertEquals(new GenericSaltError(JsonNull.INSTANCE),
                response.get("minion1").error().get());
    }

    @Test
    public final void testUpdatedb() throws SaltException {
        // First we get the call to use in the tests
        LocalCall<List<String>> call = Locate.updatedb();
        assertEquals("locate.updatedb", call.getPayload().get("fun"));

        // Test with a successful response
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_UPDATEDB_OK_RESPONSE)));

        Map<String, Result<List<String>>> response = call.callSync(client,
                new MinionList("minion1"));

        assertNotNull(response.get("minion1"));
        assertTrue(response.get("minion1").result().get().isEmpty());

        // Test with an erroneous response
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_UPDATEDB_ERROR_RESPONSE)));

        response = call.callSync(client, new MinionList("minion1"));

        assertNotNull(response.get("minion1"));
        assertNotNull(response.get("minion1").result().get());
        assertEquals(1, response.get("minion1").result().get().size());
        assertEquals("updatedb:/etc/updatedb.conf:17: unknown variable `test'",
                response.get("minion1").result().get().get(0));
    }

    @Test
    public final void testStats() throws SaltException {
        // First we get the call to use in the tests
        LocalCall<Locate.Stats> call = Locate.stats();
        assertEquals("locate.stats", call.getPayload().get("fun"));

        // Test with an erroneous response
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_STATS_OK_RESPONSE)));

        Map<String, Result<Locate.Stats>> response = call.callSync(client,
                new MinionList("minion1"));

        assertNotNull(response.get("minion1"));
        Locate.Stats minion1 = response.get("minion1").result().get();
        assertNotNull(minion1);
        assertEquals(178140L, minion1.getFiles());
        assertEquals(12070L, minion1.getDirectories());
        assertEquals(9345681L, minion1.getFileNamesBytes());
        assertEquals(3969350L, minion1.getDatabaseBytes());
        assertEquals("/var/lib/mlocate/mlocate.db", minion1.getDatabaseLocation());
    }

    @Test
    public final void testLocate() throws SaltException {
        // First we get the call to use in the tests
        LocalCall<List<String>> call = Locate.locate("ld.so.cache", Optional.empty(),
                Optional.empty(), Optional.empty());
        assertEquals("locate.locate", call.getPayload().get("fun"));

        // Test with an successful response
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_LOCATE_OK_RESPONSE)));

        Map<String, Result<List<String>>> response = call.callSync(client,
                new MinionList("minion1"));

        assertNotNull(response.get("minion1"));
        List<String> minion1 = response.get("minion1").result().get();
        assertNotNull(minion1);
        assertEquals(1, minion1.size());
        assertEquals("/etc/ld.so.cache", minion1.get(0));

        // Test with an exception
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_LOCATE_NOINPUT_RESPONSE)));

        response = call.callSync(client, new MinionList("minion1"));

        assertNotNull(response.get("minion1"));
        assertNotNull(response.get("minion1").error().get());
        assertTrue(response.get("minion1").error().get() instanceof GenericSaltError);
    }

}
