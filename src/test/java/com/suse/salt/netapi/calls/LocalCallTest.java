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

import com.suse.salt.netapi.AuthModule;
import com.suse.salt.netapi.calls.modules.Cmd;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.client.SaltClientTest;
import com.suse.salt.netapi.datatypes.Batch;
import com.suse.salt.netapi.datatypes.target.Glob;
import com.suse.salt.netapi.datatypes.target.SSHTarget;
import com.suse.salt.netapi.datatypes.target.Target;
import com.suse.salt.netapi.utils.ClientUtils;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Optional;

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
    static final String JSON_CALL_SYNC_PING_REQUEST = ClientUtils.streamToString(
            SaltClientTest.class.getResourceAsStream("/call_sync_ping_request.json"));
    static final String JSON_CALL_SYNC_PING_RESPONSE = ClientUtils.streamToString(
            SaltClientTest.class.getResourceAsStream("/call_sync_ping_response.json"));
    static final String JSON_CALL_SYNC_BATCH_PING_REQUEST = ClientUtils.streamToString(
            SaltClientTest.class.getResourceAsStream("/call_sync_batch_ping_request.json"));
    static final String JSON_CALL_SYNC_BATCH_PING_RESPONSE = ClientUtils.streamToString(
            SaltClientTest.class.getResourceAsStream(
                    "/call_sync_batch_ping_response.json"));

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
    public void testWithTimeouts() {
        LocalCall<String> run = Cmd.run("echo 'hello world'");
        assertFalse(run.getPayload().containsKey("timeout"));
        assertFalse(run.getPayload().containsKey("gather_job_timeout"));

        LocalCall<String> runWithTimeouts = run.withTimeouts(Optional.of(4),
                Optional.of(1));
        assertFalse(run.getPayload().containsKey("timeout"));
        assertFalse(run.getPayload().containsKey("gather_job_timeout"));
        assertTrue(runWithTimeouts.getPayload().containsKey("timeout"));
        assertTrue(runWithTimeouts.getPayload().containsKey("gather_job_timeout"));
        assertEquals(runWithTimeouts.getPayload().get("timeout"), 4);
        assertEquals(runWithTimeouts.getPayload().get("gather_job_timeout"), 1);

        LocalCall<String> runWithoutTimeouts = runWithTimeouts.withoutTimeouts();
        assertFalse(runWithoutTimeouts.getPayload().containsKey("timeout"));
        assertFalse(runWithoutTimeouts.getPayload().containsKey("gather_job_timeout"));
        assertTrue(runWithTimeouts.getPayload().containsKey("timeout"));
        assertTrue(runWithTimeouts.getPayload().containsKey("gather_job_timeout"));
        assertEquals(runWithTimeouts.getPayload().get("timeout"), 4);
        assertEquals(runWithTimeouts.getPayload().get("gather_job_timeout"), 1);
    }
    /**
     * Verify that system return the correct module name and function name
     */

    @Test
    public void testCorrectFunction() {
        LocalCall<String> run = Cmd.run("echo 'hello world'");
        assertEquals(run.getModuleName(), "cmd");
        assertEquals(run.getFunctionName(), "run");

    }

    /**
     * Verify that system throw IllegalArgumentException when function  is not in right
     * format.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectFunction() {
        new LocalCall<>("cmdrun", Optional.empty(),
                Optional.empty(), new TypeToken<String>(){});
    }

    /**
     * Verify correctness of the request body with an exemplary synchronous call.
     */
    @Test
    public void testCallSync() {
        stubFor(any(urlMatching("/run"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_CALL_SYNC_PING_RESPONSE)));

        LocalCall<Boolean> run = com.suse.salt.netapi.calls.modules.Test.ping();
        Target<String> target = new Glob("*");

        run.callSync(client, target, "user", "pa55wd", AuthModule.AUTO).toCompletableFuture().join();
        verify(1, postRequestedFor(urlEqualTo("/run"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
                .withRequestBody(equalToJson(JSON_CALL_SYNC_PING_REQUEST)));
    }

    /**
     * Verify correctness of the request body with an exemplary synchronous batch call.
     */
    @Test
    public void testCallSyncWithBatch() {
        stubFor(any(urlMatching("/run"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_CALL_SYNC_BATCH_PING_RESPONSE)));

        LocalCall<Boolean> run = com.suse.salt.netapi.calls.modules.Test.ping();
        Target<String> target = new Glob("*");

        run.callSync(client, target, "user", "pa55wd", AuthModule.AUTO, Batch.asAmount(1)).toCompletableFuture().join();
        verify(1, postRequestedFor(urlEqualTo("/run"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
                .withRequestBody(equalToJson(JSON_CALL_SYNC_BATCH_PING_REQUEST)));
    }

    /**
     * Verify correctness of the request body with an exemplary salt-ssh call.
     */
    @Test
    public void testCallSyncSSH() {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_SSH_PING_RESPONSE)));

        LocalCall<Boolean> run = com.suse.salt.netapi.calls.modules.Test.ping();
        SSHTarget<String> target = new Glob("*");
        SaltSSHConfig config = new SaltSSHConfig.Builder()
                .extraFilerefs("my/file/ref")
                .identitiesOnly(true)
                .ignoreHostKeys(true)
                .keyDeploy(true)
                .noHostKeys(true)
                .passwd("pa55wd")
                .priv("/home/user/.ssh/id_rsa")
                .randomThinDir(true)
                .refreshCache(true)
                .remotePortForwards("8888:my.host:443")
                .roster("flat")
                .rosterFile("/tmp/my-roster")
                .sshMaxProcs(50)
                .sudo(true)
                .user("user")
                .wipe(true)
                .build();

        run.callSyncSSH(client, target, config).toCompletableFuture().join();
        verify(1, postRequestedFor(urlEqualTo("/run"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
                .withRequestBody(equalToJson(JSON_SSH_PING_REQUEST)));
    }
}
