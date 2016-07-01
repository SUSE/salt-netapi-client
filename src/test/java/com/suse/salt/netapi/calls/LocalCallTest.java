package com.suse.salt.netapi.calls;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.suse.salt.netapi.calls.modules.Cmd;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.client.SaltClientTest;
import com.suse.salt.netapi.datatypes.target.Glob;
import com.suse.salt.netapi.datatypes.target.Target;
import com.suse.salt.netapi.exception.SaltException;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.results.SSHResult;
import com.suse.salt.netapi.utils.ClientUtils;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

/**
 * Tests for LocalCall
 */
public class LocalCallTest {

    private static final int MOCK_HTTP_PORT = 8888;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(MOCK_HTTP_PORT);

    static final String JSON_SSH_PING_REQUEST = ClientUtils.streamToString(
            SaltClientTest.class.getResourceAsStream("/ssh_ping_request.json"));
    static final String JSON_SSH_PING_RESPONSE = ClientUtils.streamToString(
            SaltClientTest.class.getResourceAsStream("/ssh_ping_response.json"));

    private SaltClient client;

    @Before
    public void init() {
        URI uri = URI.create("http://localhost:" + Integer.toString(MOCK_HTTP_PORT));
        client = new SaltClient(uri);
    }

    @Test
    public void testWithMetadata() {
        LocalCall<String> run = Cmd.run("echo 'hello world'");
        assertFalse(run.getPayload().containsKey("metadata"));

        LocalCall<String> runWithMetadata = run.withMetadata("myMetadata");
        assertFalse(run.getPayload().containsKey("metadata"));
        assertTrue(runWithMetadata.getPayload().containsKey("metadata"));
        assertEquals(runWithMetadata.getPayload().get("metadata"), "myMetadata");

        LocalCall<String> runWithoutMetadata = run.withoutMetadata();
        assertFalse(runWithoutMetadata.getPayload().containsKey("metadata"));
        assertTrue(runWithMetadata.getPayload().containsKey("metadata"));
        assertEquals(runWithMetadata.getPayload().get("metadata"), "myMetadata");
    }

    @Test
    public void testCallSyncSSH() throws SaltException {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_SSH_PING_RESPONSE)));

        LocalCall<Boolean> run = com.suse.salt.netapi.calls.modules.Test.ping();
        SaltSSHConfig config = new SaltSSHConfig.Builder()
                .rosterFile("/tmp/my-roster")
                .ignoreHostKeys(true)
                .build();
        Target<String> target = new Glob("*");
        Map<String, Result<SSHResult<Boolean>>> result =
                run.callSyncSSH(client, target, config);

        // Verify the request
        verify(1, postRequestedFor(urlEqualTo("/run"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(JSON_SSH_PING_REQUEST)));

        // Verify the result
        assertEquals("test.ping", result.get("my.minion").result().get().getFun());
        assertEquals(Collections.EMPTY_LIST,
                result.get("my.minion").result().get().getFunArgs());
        assertEquals("my.minion", result.get("my.minion").result().get().getId());
        assertEquals("20160701150655664384",
                result.get("my.minion").result().get().getJid());
        assertEquals(0, result.get("my.minion").result().get().getRetcode());
        assertTrue(result.get("my.minion").result().get().getReturn().get());
    }
}
