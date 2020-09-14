package com.suse.salt.netapi.calls.modules;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.suse.salt.netapi.calls.LocalCall;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.client.impl.HttpAsyncClientImpl;
import com.suse.salt.netapi.datatypes.AuthMethod;
import com.suse.salt.netapi.datatypes.Token;
import com.suse.salt.netapi.datatypes.target.MinionList;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.utils.ClientUtils;
import com.suse.salt.netapi.utils.TestUtils;

/**
 * Match module unit test
 */
public class MatchTest {

    private static final int MOCK_HTTP_PORT = 8888;

    private SaltClient client;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(MOCK_HTTP_PORT);

    static final String JSON_MATCH_OUTPUT = ClientUtils.streamToString(
            FileTest.class.getResourceAsStream("/modules/match/match_output.json"));

    private static final AuthMethod AUTH = new AuthMethod(new Token());

    private CloseableHttpAsyncClient closeableHttpAsyncClient;

    @Before
    public void init() {
        URI uri = URI.create("http://localhost:" + MOCK_HTTP_PORT);
        closeableHttpAsyncClient = TestUtils.defaultClient();
        client = new SaltClient(uri, new HttpAsyncClientImpl(closeableHttpAsyncClient));
    }

    @After
    public void cleanup() throws IOException {
        closeableHttpAsyncClient.close();
    }

    private static void mockOkResponseWith(String json) {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                        .withStatus(HttpURLConnection.HTTP_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBody(json)));
    }

    @Test
    public final void testCompound() {
        LocalCall<Boolean> call = Match.compound("foo", Optional.of("minion"));
        assertEquals("match.compound", call.getPayload().get("fun"));

        mockOkResponseWith(JSON_MATCH_OUTPUT);

        Map<String, Result<Boolean>> response =
                call.callSync(client, new MinionList("minion"), AUTH)
                        .toCompletableFuture().join();

        assertNotNull(response.get("minion"));

        Boolean output = response.get("minion").result().get();

        assertTrue(output);
    }

    @Test
    public final void testGlob() {
        LocalCall<Boolean> call = Match.glob("foo");
        assertEquals("match.glob", call.getPayload().get("fun"));

        mockOkResponseWith(JSON_MATCH_OUTPUT);

        Map<String, Result<Boolean>> response =
                call.callSync(client, new MinionList("minion"), AUTH)
                        .toCompletableFuture().join();

        assertNotNull(response.get("minion"));

        Boolean output = response.get("minion").result().get();

        assertTrue(output);
    }

    @Test
    public final void testGrain() {
        LocalCall<Boolean> call = Match.grain("foo", Optional.empty());
        assertEquals("match.grain", call.getPayload().get("fun"));

        mockOkResponseWith(JSON_MATCH_OUTPUT);

        Map<String, Result<Boolean>> response =
                call.callSync(client, new MinionList("minion"), AUTH)
                        .toCompletableFuture().join();

        assertNotNull(response.get("minion"));

        Boolean output = response.get("minion").result().get();

        assertTrue(output);
    }

    @Test
    public final void testPillar() {
        LocalCall<Boolean> call = Match.pillar("foo", Optional.empty());
        assertEquals("match.pillar", call.getPayload().get("fun"));

        mockOkResponseWith(JSON_MATCH_OUTPUT);

        Map<String, Result<Boolean>> response =
                call.callSync(client, new MinionList("minion"), AUTH)
                        .toCompletableFuture().join();

        assertNotNull(response.get("minion"));

        Boolean output = response.get("minion").result().get();

        assertTrue(output);
    }

    @Test
    public final void testData() {
        LocalCall<Boolean> call = Match.data("foo");
        assertEquals("match.data", call.getPayload().get("fun"));

        mockOkResponseWith(JSON_MATCH_OUTPUT);

        Map<String, Result<Boolean>> response =
                call.callSync(client, new MinionList("minion"), AUTH)
                        .toCompletableFuture().join();

        assertNotNull(response.get("minion"));

        Boolean output = response.get("minion").result().get();

        assertTrue(output);
    }

    @Test
    public final void testList() {
        LocalCall<Boolean> call = Match.list("foo", "foo2", "foo3");
        assertEquals("match.list", call.getPayload().get("fun"));

        mockOkResponseWith(JSON_MATCH_OUTPUT);

        Map<String, Result<Boolean>> response =
                call.callSync(client, new MinionList("minion"), AUTH)
                        .toCompletableFuture().join();

        assertNotNull(response.get("minion"));

        Boolean output = response.get("minion").result().get();

        assertTrue(output);
    }
}
