package com.suse.salt.netapi.calls.runner;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.Assert.assertTrue;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.suse.salt.netapi.calls.modules.SaltUtilTest;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.client.impl.HttpAsyncClientConnection;
import com.suse.salt.netapi.datatypes.AuthMethod;
import com.suse.salt.netapi.datatypes.Token;
import com.suse.salt.netapi.utils.ClientUtils;
import com.suse.salt.netapi.utils.TestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Tests for salt.runners.event
 */
public class EventTest {

    private static final int MOCK_HTTP_PORT = 8888;

    static final String JSON_EVENT_SEND_REQUEST = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/runner/event_send_request.json"));
    static final String JSON_EVENT_SEND_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/runner/event_send_response.json"));

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(MOCK_HTTP_PORT);

    static final AuthMethod AUTH = new AuthMethod(new Token());

    private SaltClient client;

    @Before
    public void init() {
        URI uri = URI.create("http://localhost:" + Integer.toString(MOCK_HTTP_PORT));
        client = new SaltClient(uri, new HttpAsyncClientConnection(TestUtils.defaultClient()));
    }

    @Test
    public void testEventSend() {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse().withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_EVENT_SEND_RESPONSE)));

        Map<String, Object> data = new HashMap<>();
        data.put("foo", "bar");
        data.put("some-value", 2);

        boolean success = Event.send("my/custom/event", Optional.of(data))
                .callSync(client, AUTH).toCompletableFuture().join().result().get();

        assertTrue(success);
        verify(1, postRequestedFor(urlEqualTo("/"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
                .withRequestBody(equalToJson(JSON_EVENT_SEND_REQUEST)));
    }
}
