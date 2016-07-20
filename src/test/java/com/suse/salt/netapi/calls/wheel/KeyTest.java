package com.suse.salt.netapi.calls.wheel;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.Assert.assertTrue;

import com.suse.salt.netapi.calls.WheelResult;
import com.suse.salt.netapi.calls.modules.SaltUtilTest;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.exception.SaltException;
import com.suse.salt.netapi.utils.ClientUtils;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Optional;

/**
 * Tests for the key wheel functions.
 */
public class KeyTest {

    private static final int MOCK_HTTP_PORT = 8888;

    static final String JSON_KEY_GEN_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/wheel/key_gen_response.json"));

    static final String JSON_KEY_GEN_ACCEPT_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/wheel/key_gen_accept_response.json"));

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(MOCK_HTTP_PORT);

    private SaltClient client;

    @Before
    public void init() {
        URI uri = URI.create("http://localhost:" + Integer.toString(MOCK_HTTP_PORT));
        client = new SaltClient(uri);
    }

    @Test
    public void testGen() throws SaltException {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_KEY_GEN_RESPONSE)));

        WheelResult<Key.Pair> keyPair = Key.gen("minion1").callSync(client);
        assertTrue(keyPair.getData().getResult().getPub().isPresent());
        assertTrue(keyPair.getData().getResult().getPriv().isPresent());

        verify(1, postRequestedFor(urlEqualTo("/"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(
                        "[{'client':'wheel', 'fun':'key.gen','id_':'minion1'}]")));
    }

    @Test
    public void testGenAccept() throws SaltException {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_KEY_GEN_ACCEPT_RESPONSE)));

        WheelResult<Key.Pair> keyPair = Key.genAccept(
                "minion1", Optional.empty()).callSync(client);
        assertTrue(keyPair.getData().getResult().getPub().isPresent());
        assertTrue(keyPair.getData().getResult().getPriv().isPresent());

        verify(1, postRequestedFor(urlEqualTo("/"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(
                        "[{'client':'wheel', 'fun':'key.gen_accept','id_':'minion1'}]")));
    }
}
