package com.suse.saltstack.netapi.client;

import com.suse.saltstack.netapi.exception.SaltStackException;
import com.suse.saltstack.netapi.datatypes.Job;
import com.suse.saltstack.netapi.datatypes.Token;
import com.suse.saltstack.netapi.utils.ClientUtils;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import java.util.Arrays;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

/**
 * SaltStack API unit tests.
 */
public class SaltStackClientTest {
    private static final int MOCK_HTTP_PORT = 8888;


    static final String JSON_START_COMMAND_RESPONSE = ClientUtils.streamToString(
            SaltStackClientTest.class.getResourceAsStream("/minions_response.json"));
    static final String JSON_LOGIN_REQUEST = ClientUtils.streamToString(
            SaltStackClientTest.class.getResourceAsStream("/login_request.json"));
    static final String JSON_LOGIN_RESPONSE =  ClientUtils.streamToString(
            SaltStackClientTest.class.getResourceAsStream("/login_response.json"));
    static final String JSON_RUN_REQUEST = ClientUtils.streamToString(
            SaltStackClientTest.class.getResourceAsStream("/run_request.json"));
    static final String JSON_RUN_RESPONSE = ClientUtils.streamToString(
            SaltStackClientTest.class.getResourceAsStream("/run_response.json"));

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(MOCK_HTTP_PORT);

    private SaltStackClient client;

    @Before
    public void init() {
        URI uri = URI.create("http://localhost:" + Integer.toString(MOCK_HTTP_PORT));
        client = new SaltStackClient(uri);
    }

    @Test
    public void testLoginOk() throws SaltStackException {
        stubFor(post(urlEqualTo("/login"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(JSON_LOGIN_REQUEST))
                .willReturn(aResponse()
                        .withStatus(HttpURLConnection.HTTP_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JSON_LOGIN_RESPONSE)));

        Token token = client.login("user", "pass");
        assertEquals("Token mismatch", token.getToken(), "f248284b655724ca8a86bcab4b8df608ebf5b08b");
        assertEquals("EAuth mismatch", token.getEauth(), "auto");
        assertEquals("User mismatch", token.getUser(), "user");
        assertEquals("Perms mismatch", token.getPerms(), Arrays.asList(".*", "@wheel"));
    }

    @Test(expected = SaltStackException.class)
    public void testLoginFailure() throws SaltStackException {
        stubFor(post(urlEqualTo("/login"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(HttpURLConnection.HTTP_UNAUTHORIZED)));
        client.login("user", "pass");
    }

    @Test
    public void testLoginAsyncOk() throws SaltStackException {
        stubFor(post(urlEqualTo("/login"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(JSON_LOGIN_REQUEST))
                .willReturn(aResponse()
                        .withStatus(HttpURLConnection.HTTP_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JSON_LOGIN_RESPONSE)));

        Future<Token> futureToken = client.loginAsync("user", "pass");
        Token token;
        try {
            token = futureToken.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new SaltStackException(e);
        }

        assertEquals("Token mismatch", token.getToken(), "f248284b655724ca8a86bcab4b8df608ebf5b08b");
        assertEquals("EAuth mismatch", token.getEauth(), "auto");
        assertEquals("User mismatch", token.getUser(), "user");
        assertEquals("Perms mismatch", token.getPerms(), Arrays.asList(".*", "@wheel"));
    }

    @Test(expected = SaltStackException.class)
    public void testLoginAsyncFailure() throws SaltStackException {
        stubFor(post(urlEqualTo("/login"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(HttpURLConnection.HTTP_UNAUTHORIZED)));

        Future<Token> futureToken = client.loginAsync("user", "pass");
        Token token;
        try {
            token = futureToken.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new SaltStackException(e);
        }
        assertNull(token);
    }

    @Test
    public void testRunRequest() throws SaltStackException {
        stubFor(post(urlEqualTo("/run"))
                .willReturn(aResponse()
                    .withStatus(HttpURLConnection.HTTP_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(JSON_RUN_RESPONSE)));

        client.run("user", "pass", "pam", "local", "*", "test.ping", null, null);

        verify(1, postRequestedFor(urlEqualTo("/run"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(JSON_RUN_REQUEST)));
    }

    @Test
    public void testRunRequestAsync() throws SaltStackException {
        stubFor(post(urlEqualTo("/run"))
                .willReturn(aResponse()
                        .withStatus(HttpURLConnection.HTTP_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JSON_RUN_RESPONSE)));

        Future<?> future = client.runAsync("user", "pass", "pam", "local", "*",
                "test.ping", null, null);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new SaltStackException(e);
        }

        verify(1, postRequestedFor(urlEqualTo("/run"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(JSON_RUN_REQUEST)));
    }

    @Test
    public void testRunResult() throws SaltStackException {
        stubFor(post(urlEqualTo("/run"))
                .willReturn(aResponse()
                    .withStatus(HttpURLConnection.HTTP_OK)
                    .withHeader("Content-Type", "application/json")
                    .withBody(JSON_RUN_RESPONSE)));

        Map<String, Object> retvals = client.run("user", "pass", "pam", "local", "*",
                "test.ping", null, null);

        assertNotNull(retvals);
        assertTrue(retvals.containsKey("minion-1"));
        assertEquals(retvals.get("minion-1"), true);
    }

    @Test
    public void testRunResultAsync() throws SaltStackException {
        stubFor(post(urlEqualTo("/run"))
                .willReturn(aResponse()
                        .withStatus(HttpURLConnection.HTTP_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JSON_RUN_RESPONSE)));

        Future<Map<String, Object>> future = client.runAsync("user", "pass",
                "pam", "local", "*", "test.ping", null, null);
        Map<String, Object> retvals;
        try {
            retvals = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new SaltStackException(e);
        }

        assertNotNull(retvals);
        assertTrue(retvals.containsKey("minion-1"));
        assertEquals(retvals.get("minion-1"), true);
    }

    @Test
    public void testStartCommand() throws SaltStackException {
        stubFor(post(urlEqualTo("/minions")).willReturn(
                aResponse().withStatus(HttpURLConnection.HTTP_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JSON_START_COMMAND_RESPONSE)));

        Job job = client.startCommand("*", "test.ping", null, null);

        assertNotNull(job);
        assertEquals(job.getJid(), "20150211105524392307");
        assertEquals(job.getMinions(), Arrays.asList("myminion"));
    }
}
