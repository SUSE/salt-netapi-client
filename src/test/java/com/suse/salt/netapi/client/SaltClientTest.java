package com.suse.salt.netapi.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.suse.salt.netapi.AuthModule.AUTO;
import static com.suse.salt.netapi.AuthModule.PAM;
import static com.suse.salt.netapi.config.ClientConfig.SOCKET_TIMEOUT;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.suse.salt.netapi.calls.SaltSSHConfig;
import com.suse.salt.netapi.client.impl.JDKConnectionFactory;
import com.suse.salt.netapi.datatypes.Token;
import com.suse.salt.netapi.datatypes.cherrypy.Stats;
import com.suse.salt.netapi.datatypes.target.Glob;
import com.suse.salt.netapi.exception.SaltException;
import com.suse.salt.netapi.exception.SaltUserUnauthorizedException;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.results.SSHRawResult;
import com.suse.salt.netapi.utils.ClientUtils;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Salt API client unit tests.
 */
public class SaltClientTest {

    private static final int MOCK_HTTP_PORT = 8888;

    static final String JSON_LOGIN_REQUEST = ClientUtils.streamToString(
            SaltClientTest.class.getResourceAsStream("/login_request.json"));
    static final String JSON_LOGIN_RESPONSE = ClientUtils.streamToString(
            SaltClientTest.class.getResourceAsStream("/login_response.json"));
    static final String JSON_RUN_REQUEST = ClientUtils.streamToString(
            SaltClientTest.class.getResourceAsStream("/run_request.json"));
    static final String JSON_SSHRAW_RUN_REQUEST = ClientUtils.streamToString(
            SaltClientTest.class.getResourceAsStream("/ssh_raw_run_request.json"));
    static final String JSON_RUN_RESPONSE = ClientUtils.streamToString(
            SaltClientTest.class.getResourceAsStream("/run_response.json"));
    static final String JSON_SSHRAW_RUN_RESPONSE = ClientUtils.streamToString(
            SaltClientTest.class.getResourceAsStream("/ssh_raw_run_response.json"));
    static final String JSON_STATS_RESPONSE = ClientUtils.streamToString(
            SaltClientTest.class.getResourceAsStream("/stats_response.json"));
    static final String JSON_HOOK_RESPONSE = ClientUtils.streamToString(
            SaltClientTest.class.getResourceAsStream("/hook_response.json"));
    static final String JSON_LOGOUT_RESPONSE = ClientUtils.streamToString(
            SaltClientTest.class.getResourceAsStream("/logout_response.json"));

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(MOCK_HTTP_PORT);

    private SaltClient client;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void init() {
        URI uri = URI.create("http://localhost:" + Integer.toString(MOCK_HTTP_PORT));
        client = new SaltClient(uri);
    }

    @Test
    public void testLoginOk() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_LOGIN_RESPONSE)));

        Token token = client.login("user", "pass", AUTO);
        verifyLoginToken(token);
    }

    @Test
    public void testLoginAsyncOk() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_LOGIN_RESPONSE)));

        Future<Token> futureToken = client.loginAsync("user", "pass", AUTO);
        Token token = futureToken.get();
        verifyLoginToken(token);
    }

    private void verifyLoginToken(Token token) {
        verify(1, postRequestedFor(urlEqualTo("/login"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
                .withRequestBody(equalToJson(JSON_LOGIN_REQUEST)));

        assertEquals("Token mismatch",
                "f248284b655724ca8a86bcab4b8df608ebf5b08b", token.getToken());
        assertEquals("EAuth mismatch", "auto", token.getEauth());
        assertEquals("User mismatch", "user", token.getUser());
        assertEquals("Perms mismatch", Arrays.asList(".*", "@wheel"), token.getPerms());
    }

    @Test(expected = SaltUserUnauthorizedException.class)
    public void testLoginFailure() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_UNAUTHORIZED)));
        client.login("user", "pass", AUTO);
    }

    @Test(expected = ExecutionException.class)
    public void testLoginAsyncFailure() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_UNAUTHORIZED)));

        Future<Token> futureToken = client.loginAsync("user", "pass", AUTO);
        Token token = futureToken.get();
        assertNull(token);
    }

    @Test
    public void testRunRequest() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_RUN_RESPONSE)));

        List<Object> args = new ArrayList<>();
        args.add("i3");

        Map<String, Object> kwargs = new LinkedHashMap<>();
        kwargs.put("refresh", "true");
        kwargs.put("sysupgrade", "false");

        Map<String, Object> retvals =
                client.run("user", "pass", PAM, "local", new Glob(),
                "pkg.install", args, kwargs);
        verifyRunResults(retvals);
    }

    @Test
    public void testRunRawSSHCommand() throws SaltException {
        Map<String, Result<SSHRawResult>> retvals = null;

        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse().withStatus(HttpURLConnection.HTTP_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JSON_SSHRAW_RUN_RESPONSE)));

        retvals = client.runRawSSHCommand("uptime", new Glob(),
                new SaltSSHConfig.Builder().build());

        SSHRawResult expectedResult = new SSHRawResult(0,
                "Password: \n 17:42pm  up  19:16,  0 users,  "
                        + "load average: 0.00, 0.01, 0.05\n",
                "Could not create directory '/srv/salt/.ssh'.\r\n"
                        + "Failed to add the host to the list of known hosts"
                        + " (/srv/salt/.ssh/known_hosts).\r\n");
        assertNotNull(retvals);
        assertEquals(expectedResult, retvals.get("sumarm30").result().orElseGet(null));
        verify(1,
                postRequestedFor(urlEqualTo("/run"))
                    .withHeader("Accept", equalTo("application/json"))
                    .withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
                    .withRequestBody(equalToJson(JSON_SSHRAW_RUN_REQUEST)));
    }

    @Test
    public void testRunRequestAsync() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_RUN_RESPONSE)));

        List<Object> args = new ArrayList<>();
        args.add("i3");

        Map<String, Object> kwargs = new LinkedHashMap<>();
        kwargs.put("refresh", "true");
        kwargs.put("sysupgrade", "false");

        Future<Map<String, Object>> future = client.runAsync("user", "pass",
                PAM, "local", new Glob(), "pkg.install", args, kwargs);
        Map<String, Object> retvals = future.get();
        verifyRunResults(retvals);
    }

    private void verifyRunResults(Map<String, Object> retvals) {
        verify(1, postRequestedFor(urlEqualTo("/run"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
                .withRequestBody(equalToJson(JSON_RUN_REQUEST)));

        LinkedHashMap<String, String> i3 = new LinkedHashMap<>();
        i3.put("new", "4.10.3-1");
        i3.put("old", "");

        LinkedHashMap<String, String> i3lock = new LinkedHashMap<>();
        i3lock.put("new", "2.7-1");
        i3lock.put("old", "");

        LinkedHashMap<String, String> i3status = new LinkedHashMap<>();
        i3status.put("new", "2.9-2");
        i3status.put("old", "");

        Map<String, Map<String, String>> expected = new LinkedHashMap<>();
        expected.put("i3", i3);
        expected.put("i3lock", i3lock);
        expected.put("i3status", i3status);

        assertNotNull(retvals);
        assertTrue(retvals.containsKey("minion-1"));
        assertEquals(expected, retvals.get("minion-1"));
    }

    @Test
    public void testRunRequestWithSocketTimeout() throws Exception {
        exception.expect(SaltException.class);
        exception.expectMessage(containsString("Read timed out"));

        // create a local SaltClient with a fast timeout configuration
        // to do not lock tests more than 2s
        URI uri = URI.create("http://localhost:" + Integer.toString(MOCK_HTTP_PORT));
        SaltClient clientWithFastTimeout = new SaltClient(uri);
        clientWithFastTimeout.getConfig().put(SOCKET_TIMEOUT, 1000);

        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withFixedDelay(2000)));

        clientWithFastTimeout.login("user", "pass", AUTO);
    }

    @Test
    public void testRunRequestWithSocketTimeoutThroughJDKConnection() throws Exception {
        exception.expect(SaltException.class);
        exception.expectMessage(containsString("Read timed out"));

        // create a local SaltClient with a fast timeout configuration
        // to do not lock tests more than 2s
        URI uri = URI.create("http://localhost:" + Integer.toString(MOCK_HTTP_PORT));
        SaltClient clientWithFastTimeout = new SaltClient(uri,
                new JDKConnectionFactory());
        clientWithFastTimeout.getConfig().put(SOCKET_TIMEOUT, 1000);

        stubFor(post(urlEqualTo("/login"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                .withFixedDelay(2000)));

        clientWithFastTimeout.login("user", "pass", AUTO);
    }

    @Test
    public void testStats() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_STATS_RESPONSE)));

        Stats stats = client.stats();

        assertNotNull(stats);
        verify(1, getRequestedFor(urlEqualTo("/stats"))
                .withHeader("Accept", equalTo("application/json"))
                .withRequestBody(equalTo("")));
    }

    @Test
    public void testStatsAsync() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_STATS_RESPONSE)));

        Stats stats = client.statsAsync().get();

        assertNotNull(stats);
        verify(1, getRequestedFor(urlEqualTo("/stats"))
                .withHeader("Accept", equalTo("application/json"))
                .withRequestBody(equalTo("")));
    }

    @Test
    public void testSendEvent() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_HOOK_RESPONSE)));

        JsonObject json = new JsonObject();
        json.addProperty("foo", "bar");
        JsonArray array = new JsonArray();
        array.add(new JsonPrimitive("one"));
        array.add(new JsonPrimitive("two"));
        array.add(new JsonPrimitive("three"));
        json.add("list", array);

        String data = json.toString();

        boolean success = client.sendEvent("my/tag", data);

        assertTrue(success);
        verify(1, postRequestedFor(urlEqualTo("/hook/my/tag"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
                .withRequestBody(equalTo(data)));
    }

    @Test
    public void testSendEventAsync() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_HOOK_RESPONSE)));

        JsonObject json = new JsonObject();
        json.addProperty("foo", "bar");
        JsonArray array = new JsonArray();
        array.add(new JsonPrimitive("one"));
        array.add(new JsonPrimitive("two"));
        array.add(new JsonPrimitive("three"));
        json.add("list", array);

        String data = json.toString();

        boolean success = client.sendEventAsync("my/tag", data).get();

        assertTrue(success);
        verify(1, postRequestedFor(urlEqualTo("/hook/my/tag"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
                .withRequestBody(equalTo(data)));
    }

    @Test
    public void testLogout() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_LOGOUT_RESPONSE)));

        boolean success = client.logout();
        verifyLogout(success);
    }

    @Test
    public void testLogoutAsync() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_LOGOUT_RESPONSE)));

        boolean success = client.logoutAsync().get();
        verifyLogout(success);
    }

    private void verifyLogout(boolean success) {
        assertTrue(success);
        verify(1, postRequestedFor(urlEqualTo("/logout"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
                .withRequestBody(equalTo("")));
    }
}
