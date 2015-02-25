package com.suse.saltstack.netapi.client;

import com.suse.saltstack.netapi.exception.SaltStackException;
import com.suse.saltstack.netapi.datatypes.Token;

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

    final String JSON_LOGIN_REQUEST = "{\"username\":\"user\"," +
            "\"password\":\"pass\",\"eauth\":\"auto\"}";
    final String JSON_LOGIN_RESPONSE = "{\"return\": [{\"perms\": [\".*\"], " +
            "\"start\": 1422803163.765152, " +
            "\"token\": \"2fea67bb673e012f11ca7cad0d1079ccf1decaa2\", " +
            "\"expire\": 1422846363.765152, " +
            "\"user\": \"user\", \"eauth\": \"auto\"}]}";

    final String JSON_RUN_REQUEST = "[{\"username\":\"user\",\"password\":\"pass\"" +
            ",\"eauth\":\"pam\",\"client\":\"local\",\"tgt\":\"*\",\"fun\":" +
            "\"test.ping\"}]";
    final String JSON_RUN_RESPONSE = "{\"return\": [{\"minion-1\": true}]}";

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
                .withRequestBody(equalTo(JSON_LOGIN_REQUEST))
                .willReturn(aResponse()
                        .withStatus(HttpURLConnection.HTTP_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JSON_LOGIN_RESPONSE)));

        Token token = client.login("user", "pass");
        assertEquals("Token mismatch", token.getToken(), "2fea67bb673e012f11ca7cad0d1079ccf1decaa2");
        assertEquals("EAuth mismatch", token.getEauth(), "auto");
        assertEquals("User mismatch", token.getUser(), "user");
        assertEquals("Perms mismatch", token.getPerms(), Arrays.asList(".*"));
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
                .withRequestBody(equalTo(JSON_LOGIN_REQUEST))
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

        assertEquals("Token mismatch", token.getToken(), "2fea67bb673e012f11ca7cad0d1079ccf1decaa2");
        assertEquals("EAuth mismatch", token.getEauth(), "auto");
        assertEquals("User mismatch", token.getUser(), "user");
        assertEquals("Perms mismatch", token.getPerms(), Arrays.asList(".*"));
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
                .withRequestBody(equalTo(JSON_RUN_REQUEST)));
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
                .withRequestBody(equalTo(JSON_RUN_REQUEST)));
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
}
