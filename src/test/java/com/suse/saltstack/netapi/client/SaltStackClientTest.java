package com.suse.saltstack.netapi.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.suse.saltstack.netapi.datatypes.Job;
import com.suse.saltstack.netapi.datatypes.cherrypy.Stats;
import com.suse.saltstack.netapi.exception.SaltStackException;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.JsonSyntaxException;
import com.suse.saltstack.netapi.client.impl.JDKConnectionFactory;
import com.suse.saltstack.netapi.datatypes.Keys;
import com.suse.saltstack.netapi.datatypes.ScheduledJob;
import com.suse.saltstack.netapi.datatypes.Token;
import com.suse.saltstack.netapi.utils.ClientUtils;

import static com.suse.saltstack.netapi.config.ClientConfig.SOCKET_TIMEOUT;
import static com.suse.saltstack.netapi.AuthModule.PAM;
import static com.suse.saltstack.netapi.AuthModule.AUTO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.HttpURLConnection;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * SaltStack API unit tests.
 */
public class SaltStackClientTest {

    private static final int MOCK_HTTP_PORT = 8888;

    static final String JSON_START_COMMAND_REQUEST = ClientUtils.streamToString(
            SaltStackClientTest.class.getResourceAsStream("/minions_request.json"));
    static final String JSON_START_COMMAND_RESPONSE = ClientUtils.streamToString(
            SaltStackClientTest.class.getResourceAsStream("/minions_response.json"));
    static final String JSON_GET_MINIONS_RESPONSE = ClientUtils.streamToString(
            SaltStackClientTest.class.getResourceAsStream("/get_minions_response.json"));
    static final String JSON_GET_MINION_DETAILS_RESPONSE = ClientUtils.streamToString(
            SaltStackClientTest.class.getResourceAsStream("/minion_details_response.json"));
    static final String JSON_LOGIN_REQUEST = ClientUtils.streamToString(
            SaltStackClientTest.class.getResourceAsStream("/login_request.json"));
    static final String JSON_LOGIN_RESPONSE = ClientUtils.streamToString(
            SaltStackClientTest.class.getResourceAsStream("/login_response.json"));
    static final String JSON_RUN_REQUEST = ClientUtils.streamToString(
            SaltStackClientTest.class.getResourceAsStream("/run_request.json"));
    static final String JSON_RUN_RESPONSE = ClientUtils.streamToString(
            SaltStackClientTest.class.getResourceAsStream("/run_response.json"));
    static final String JSON_STATS_RESPONSE = ClientUtils.streamToString(
            SaltStackClientTest.class.getResourceAsStream("/stats_response.json"));
    static final String JSON_KEYS_RESPONSE = ClientUtils.streamToString(
            SaltStackClientTest.class.getResourceAsStream("/keys_response.json"));
    static final String JSON_JOBS_RESPONSE = ClientUtils.streamToString(
            SaltStackClientTest.class.getResourceAsStream("/jobs_response.json"));
    static final String JSON_JOBS_INVALID_START_TIME_RESPONSE = ClientUtils.streamToString(
            SaltStackClientTest.class.getResourceAsStream(
            "/jobs_response_invalid_start_time.json"));
    static final String JSON_JOBS_NULL_START_TIME_RESPONSE = ClientUtils.streamToString(
            SaltStackClientTest.class.getResourceAsStream(
            "/jobs_response_null_start_time.json"));
    static final String JSON_HOOK_RESPONSE = ClientUtils.streamToString(
            SaltStackClientTest.class.getResourceAsStream("/hook_response.json"));

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(MOCK_HTTP_PORT);

    private SaltStackClient client;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void init() {
        URI uri = URI.create("http://localhost:" + Integer.toString(MOCK_HTTP_PORT));
        client = new SaltStackClient(uri);
    }

    @Test
    public void testLoginOk() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_LOGIN_RESPONSE)));

        Token token = client.login("user", "pass", AUTO);

        verify(1, postRequestedFor(urlEqualTo("/login"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(JSON_LOGIN_REQUEST)));

        assertEquals("Token mismatch",
                token.getToken(), "f248284b655724ca8a86bcab4b8df608ebf5b08b");
        assertEquals("EAuth mismatch", token.getEauth(), "auto");
        assertEquals("User mismatch", token.getUser(), "user");
        assertEquals("Perms mismatch", token.getPerms(), Arrays.asList(".*", "@wheel"));
    }

    @Test(expected = SaltStackException.class)
    public void testLoginFailure() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_UNAUTHORIZED)));
        client.login("user", "pass", AUTO);
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

        verify(1, postRequestedFor(urlEqualTo("/login"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(JSON_LOGIN_REQUEST)));

        assertEquals("Token mismatch",
                token.getToken(), "f248284b655724ca8a86bcab4b8df608ebf5b08b");
        assertEquals("EAuth mismatch", token.getEauth(), "auto");
        assertEquals("User mismatch", token.getUser(), "user");
        assertEquals("Perms mismatch", token.getPerms(), Arrays.asList(".*", "@wheel"));
    }

    @Test(expected = ExecutionException.class)
    public void testLoginAsyncFailure() throws Exception {
        stubFor(any(urlMatching("*."))
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

        List<String> args = new ArrayList<>();
        args.add("i3");

        Map<String, String> kwargs = new LinkedHashMap<String, String>() {
            {
                put("refresh", "true");
                put("sysupgrade", "false");
            }
        };

        Map<String, Object> retvals =
                client.run("user", "pass", PAM, "local", "*", "pkg.install", args, kwargs);

        verify(1, postRequestedFor(urlEqualTo("/run"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(JSON_RUN_REQUEST)));

        Map<String, Map<String, String>> expected =
                new LinkedHashMap<String, Map<String, String>>(){
            {
                put("i3-wm", new LinkedHashMap<String, String>(){
                    {
                        put("new", "4.10.1-1");
                        put("old", "");
                    }
                });
                put("i3lock", new LinkedHashMap<String, String>(){
                    {
                        put("new", "2.6-1");
                        put("old", "");
                    }
                });
                put("i3status", new LinkedHashMap<String, String>(){
                    {
                        put("new", "2.9-1");
                        put("old", "");
                    }
                });
            }
        };

        assertNotNull(retvals);
        assertTrue(retvals.containsKey("minion-1"));
        assertEquals(retvals.get("minion-1"), expected);
    }

    @Test
    public void testRunRequestAsync() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_RUN_RESPONSE)));

        List<String> args = new ArrayList<>();
        args.add("i3");

        Map<String, String> kwargs = new LinkedHashMap<String, String>() {
            {
                put("refresh", "true");
                put("sysupgrade", "false");
            }
        };

        Future<Map<String, Object>> future = client.runAsync("user", "pass",
                PAM, "local", "*", "pkg.install", args, kwargs);
        Map<String, Object> retvals = future.get();

        verify(1, postRequestedFor(urlEqualTo("/run"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(JSON_RUN_REQUEST)));


        Map<String, Map<String, String>> expected =
                new LinkedHashMap<String, Map<String, String>>(){
            {
                put("i3-wm", new LinkedHashMap<String, String>(){
                    {
                        put("new", "4.10.1-1");
                        put("old", "");
                    }
                });
                put("i3lock", new LinkedHashMap<String, String>(){
                    {
                        put("new", "2.6-1");
                        put("old", "");
                    }
                });
                put("i3status", new LinkedHashMap<String, String>(){
                    {
                        put("new", "2.9-1");
                        put("old", "");
                    }
                });
            }
        };

        assertNotNull(retvals);
        assertTrue(retvals.containsKey("minion-1"));
        assertEquals(retvals.get("minion-1"), expected);
    }

    @Test
    public void testRunRequestWithSocketTimeout() throws Exception {
        exception.expect(SaltStackException.class);
        exception.expectMessage(containsString("Read timed out"));

        // create a local SaltStackClient with a fast timeout configuration
        // to do not lock tests more than 2s
        URI uri = URI.create("http://localhost:" + Integer.toString(MOCK_HTTP_PORT));
        SaltStackClient clientWithFastTimeout = new SaltStackClient(uri);
        clientWithFastTimeout.getConfig().put(SOCKET_TIMEOUT, 1000);

        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withFixedDelay(2000)));

        clientWithFastTimeout.login("user", "pass", AUTO);
    }

    @Test
    public void testRunRequestWithSocketTimeoutThroughJDKConnection() throws Exception {
        exception.expect(SaltStackException.class);
        exception.expectMessage(containsString("Read timed out"));

        // create a local SaltStackClient with a fast timeout configuration
        // to do not lock tests more than 2s
        URI uri = URI.create("http://localhost:" + Integer.toString(MOCK_HTTP_PORT));
        SaltStackClient clientWithFastTimeout = new SaltStackClient(uri,
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
    @SuppressWarnings("unchecked")
    public void testGetMinions() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Accept", "application/json")
                .withBody(JSON_GET_MINIONS_RESPONSE)));

        Map<String, Map<String, Object>> minions = client.getMinions();

        verify(1, getRequestedFor(urlEqualTo("/minions"))
                .withHeader("Accept", equalTo("application/json")));

        assertNotNull(minions);
        assertEquals(2, minions.size());

        assertTrue(minions.containsKey("minion1"));
        assertTrue(minions.containsKey("minion2"));

        Map<String, Object> minion1 = minions.get("minion1");
        assertEquals(56, minion1.size());
        assertEquals("VirtualBox", minion1.get("biosversion"));

        assertTrue(minion1.get("saltversioninfo") instanceof List);
        List<String> saltVersionInfo = (List<String>) minion1.get("saltversioninfo");
        assertEquals(2014.0, saltVersionInfo.get(0));
        assertEquals(7.0, saltVersionInfo.get(1));
        assertEquals(5.0, saltVersionInfo.get(2));
        assertEquals(0.0, saltVersionInfo.get(3));

        assertTrue(minion1.get("locale_info") instanceof Map);
        Map<String, String> localeInfo = ((Map<String, String>) minion1.get("locale_info"));
        assertEquals("en_US", localeInfo.get("defaultlanguage"));
        assertEquals("UTF-8", localeInfo.get("defaultencoding"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetMinionsAsync() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Accept", "application/json")
                .withBody(JSON_GET_MINIONS_RESPONSE)));

        Future<Map<String, Map<String, Object>>> future = client.getMinionsAsync();
        Map<String, Map<String, Object>> minions = future.get();

        verify(1, getRequestedFor(urlEqualTo("/minions"))
                .withHeader("Accept", equalTo("application/json")));

        assertNotNull(minions);
        assertEquals(2, minions.size());

        assertTrue(minions.containsKey("minion1"));
        assertTrue(minions.containsKey("minion2"));

        Map<String, Object> minion1 = minions.get("minion1");
        assertEquals(56, minion1.size());
        assertEquals("VirtualBox", minion1.get("biosversion"));

        assertTrue(minion1.get("saltversioninfo") instanceof List);
        List<String> saltVersionInfo = (List<String>) minion1.get("saltversioninfo");
        assertEquals(2014.0, saltVersionInfo.get(0));
        assertEquals(7.0, saltVersionInfo.get(1));
        assertEquals(5.0, saltVersionInfo.get(2));
        assertEquals(0.0, saltVersionInfo.get(3));

        assertTrue(minion1.get("locale_info") instanceof Map);
        Map<String, String> localeInfo = ((Map<String, String>) minion1.get("locale_info"));
        assertEquals("en_US", localeInfo.get("defaultlanguage"));
        assertEquals("UTF-8", localeInfo.get("defaultencoding"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetMinionDetails() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Accept", "application/json")
                .withBody(JSON_GET_MINION_DETAILS_RESPONSE)));

        Map<String, Object> minion = client.getMinionDetails("minion2");

        verify(1, getRequestedFor(urlEqualTo("/minions/minion2"))
                .withHeader("Accept", equalTo("application/json")));

        assertNotNull(minion);

        assertEquals(56, minion.size());
        assertEquals("VirtualBox", minion.get("biosversion"));

        assertTrue(minion.get("saltversioninfo") instanceof List);
        List<String> saltVersionInfo = (List<String>) minion.get("saltversioninfo");
        assertEquals(2014.0, saltVersionInfo.get(0));
        assertEquals(7.0, saltVersionInfo.get(1));
        assertEquals(5.0, saltVersionInfo.get(2));
        assertEquals(0.0, saltVersionInfo.get(3));

        assertTrue(minion.get("locale_info") instanceof Map);
        Map<String, String> localeInfo = ((Map<String, String>) minion.get("locale_info"));
        assertEquals("en_US", localeInfo.get("defaultlanguage"));
        assertEquals("UTF-8", localeInfo.get("defaultencoding"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetMinionDetailsAsync() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Accept", "application/json")
                .withBody(JSON_GET_MINION_DETAILS_RESPONSE)));

        Future<Map<String, Object>> future = client.getMinionDetailsAsync("minion2");
        Map<String, Object> minion = future.get();

        verify(1, getRequestedFor(urlEqualTo("/minions/minion2"))
                .withHeader("Accept", equalTo("application/json")));

        assertNotNull(minion);

        assertEquals(56, minion.size());
        assertEquals("VirtualBox", minion.get("biosversion"));

        assertTrue(minion.get("saltversioninfo") instanceof List);
        List<String> saltVersionInfo = (List<String>) minion.get("saltversioninfo");
        assertEquals(2014.0, saltVersionInfo.get(0));
        assertEquals(7.0, saltVersionInfo.get(1));
        assertEquals(5.0, saltVersionInfo.get(2));
        assertEquals(0.0, saltVersionInfo.get(3));

        assertTrue(minion.get("locale_info") instanceof Map);
        Map<String, String> localeInfo = ((Map<String, String>) minion.get("locale_info"));
        assertEquals("en_US", localeInfo.get("defaultlanguage"));
        assertEquals("UTF-8", localeInfo.get("defaultencoding"));
    }

    @Test
    public void testStartCommand() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_START_COMMAND_RESPONSE)));

        List<String> args = new ArrayList<>();
        args.add("i3");

        Map<String, String> kwargs = new LinkedHashMap<String, String>() {
            {
                put("refresh", "true");
                put("sysupgrade", "false");
            }
        };

        ScheduledJob job = client.startCommand("*", "pkg.install", args, kwargs);

        verify(1, postRequestedFor(urlEqualTo("/minions"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(JSON_START_COMMAND_REQUEST)));

        assertNotNull(job);
        assertEquals(job.getJid(), "20150211105524392307");
        assertEquals(job.getMinions(), Arrays.asList("myminion"));
    }

    @Test
    public void testQueryJobResult() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_RUN_RESPONSE)));

        Map<String, Object> retvals = client.getJobResult("some-job-id");

        verify(1, getRequestedFor(urlEqualTo("/jobs/some-job-id"))
                .withHeader("Accept", equalTo("application/json")));


        Map<String, Map<String, String>> expected =
                new LinkedHashMap<String, Map<String, String>>(){
            {
                put("i3-wm", new LinkedHashMap<String, String>(){
                    {
                        put("new", "4.10.1-1");
                        put("old", "");
                    }
                });
                put("i3lock", new LinkedHashMap<String, String>(){
                    {
                        put("new", "2.6-1");
                        put("old", "");
                    }
                });
                put("i3status", new LinkedHashMap<String, String>(){
                    {
                        put("new", "2.9-1");
                        put("old", "");
                    }
                });
            }
        };

        assertNotNull(retvals);
        assertTrue(retvals.containsKey("minion-1"));
        assertEquals(retvals.get("minion-1"), expected);
    }

    @Test
    public void testStartCommandAsync() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_START_COMMAND_RESPONSE)));

        List<String> args = new ArrayList<>();
        args.add("i3");

        Map<String, String> kwargs = new LinkedHashMap<String, String>() {
            {
                put("refresh", "true");
                put("sysupgrade", "false");
            }
        };

        Future<ScheduledJob> future = client.startCommandAsync("*", "pkg.install", args,
                kwargs);
        ScheduledJob job = future.get();

        verify(1, postRequestedFor(urlEqualTo("/minions"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(JSON_START_COMMAND_REQUEST)));

        assertNotNull(job);
        assertEquals(job.getJid(), "20150211105524392307");
        assertEquals(job.getMinions(), Arrays.asList("myminion"));
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
    public void testKeys() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_KEYS_RESPONSE)));

        Keys keys = client.keys();

        assertNotNull(keys);
        verify(1, getRequestedFor(urlEqualTo("/keys"))
                .withHeader("Accept", equalTo("application/json"))
                .withRequestBody(equalTo("")));
    }

    @Test
    public void testKeysAsync() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_KEYS_RESPONSE)));

        Keys keys = client.keysAsync().get();

        assertNotNull(keys);
        verify(1, getRequestedFor(urlEqualTo("/keys"))
                .withHeader("Accept", equalTo("application/json"))
                .withRequestBody(equalTo("")));
    }

    @Test
    public void testJobs() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_JOBS_RESPONSE)));

        Map<String, Job> jobs = client.getJobs();

        assertNotNull(jobs);
        Job job1 = jobs.get("20150304192951636258");
        Job job2 = jobs.get("20150304200110485012");
        assertEquals(Arrays.asList("enable-autodestruction"),
                job2.getArguments().getArgs());
        assertEquals(0, job1.getArguments().getArgs().size());
        assertEquals("2015-03-04 19:29:51", DATE_FORMAT.format(job1.getStartTime()));
        assertEquals("2015-03-04 20:01:10", DATE_FORMAT.format(job2.getStartTime()));
        verify(1, getRequestedFor(urlEqualTo("/jobs"))
                .withHeader("Accept", equalTo("application/json"))
                .withRequestBody(equalTo("")));
    }

    @Test(expected = JsonSyntaxException.class)
    public void testJobsWithInvalidStartTime() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_JOBS_INVALID_START_TIME_RESPONSE)));
        client.getJobs();
    }

    @Test
    public void testJobsWithNullStartTime() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_JOBS_NULL_START_TIME_RESPONSE)));

        Map<String, Job> jobs = client.getJobs();

        assertNotNull(jobs);
        Job job1 = jobs.get("20150304192951636258");
        Job job2 = jobs.get("20150304200110485012");
        assertNull(job1.getStartTime());
        assertNull(job2.getStartTime());
        verify(1, getRequestedFor(urlEqualTo("/jobs"))
                .withHeader("Accept", equalTo("application/json"))
                .withRequestBody(equalTo("")));
    }

    @Test
    public void testJobsAsync() throws Exception {
        stubFor(any(urlMatching(".*"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_JOBS_RESPONSE)));

        Map<String, Job> jobs = client.getJobsAsync().get();

        assertNotNull(jobs);
        assertEquals(Arrays.asList("enable-autodestruction"),
                jobs.get("20150304200110485012").getArguments().getArgs());
        verify(1, getRequestedFor(urlEqualTo("/jobs"))
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
                .withHeader("Content-Type", equalTo("application/json"))
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
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalTo(data)));
    }

}
