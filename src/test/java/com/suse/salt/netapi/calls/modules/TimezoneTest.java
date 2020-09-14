package com.suse.salt.netapi.calls.modules;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.JsonNull;
import com.suse.salt.netapi.calls.LocalCall;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.client.impl.HttpAsyncClientImpl;
import com.suse.salt.netapi.datatypes.AuthMethod;
import com.suse.salt.netapi.datatypes.Token;
import com.suse.salt.netapi.datatypes.target.MinionList;
import com.suse.salt.netapi.errors.JsonParsingError;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.utils.ClientUtils;
import com.suse.salt.netapi.utils.TestUtils;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.Assert.assertEquals;

/**
 * Timezone module unit tests.
 */
public class TimezoneTest {

    private static final int MOCK_HTTP_PORT = 8888;

    private static final String JSON_NULL_RESPONSE = ClientUtils.streamToString(
            TimezoneTest.class.getResourceAsStream(
            "/modules/timezone/null_response.json"));

    private static final String JSON_GETOFFSET_OK_RESPONSE = ClientUtils.streamToString(
            TimezoneTest.class.getResourceAsStream(
            "/modules/timezone/get_offset_ok.json"));

    private SaltClient client;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(MOCK_HTTP_PORT);

    static final AuthMethod AUTH = new AuthMethod(new Token());

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

    @Test
    public final void testGetOffsetNUllResponse() {
        // Test for successful null response
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_NULL_RESPONSE)));

        LocalCall<String> call = Timezone.getOffset();
        assertEquals("timezone.get_offset", call.getPayload().get("fun"));

        Map<String, Result<String>> response = call.callSync(client,
                new MinionList("minion1"), AUTH).toCompletableFuture().join();
        assertEquals(JsonNull.INSTANCE,
                ((JsonParsingError) response.get("minion1").error().get()).getJson());
    }

    @Test
    public final void testGetOffsetSuccessResponse() {
        // Test for successful responses
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                        .withStatus(HttpURLConnection.HTTP_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JSON_GETOFFSET_OK_RESPONSE)));

        LocalCall<String> call = Timezone.getOffset();
        assertEquals("timezone.get_offset", call.getPayload().get("fun"));

        Map<String, Result<String>> response = call.callSync(client,
                new MinionList("minion1"), AUTH).toCompletableFuture().join();
        assertEquals("+0100", response.get("minion1").result().get());
    }
}
