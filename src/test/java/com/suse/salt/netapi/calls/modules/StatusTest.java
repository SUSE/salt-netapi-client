package com.suse.salt.netapi.calls.modules;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;

/**
 * Status module unit tests.
 */
public class StatusTest {

    private static final int MOCK_HTTP_PORT = 8888;

    static final String JSON_MEMINFO_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/status/meminfo.json"));

    static final String JSON_LOADAVG_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/status/loadavg.json"));

    static final String JSON_DISKUSAGE_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/status/diskusage.json"));

    static final String JSON_DISKSTATS_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/status/diskstats.json"));

    static final String JSON_UPTIME_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/status/uptime.json"));

    private SaltClient client;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(MOCK_HTTP_PORT);

    static final AuthMethod AUTH = new AuthMethod(new Token());

    @Before
    public void init() {
        URI uri = URI.create("http://localhost:" + Integer.toString(MOCK_HTTP_PORT));
        client = new SaltClient(uri, new HttpAsyncClientImpl(TestUtils.defaultClient()));
    }

    @Test
    public final void testMeminfo() {
        // First we get the call to use in the tests
        LocalCall<Map<String, Map<String, Object>>> call = Status.meminfo();
        assertEquals("status.meminfo", call.getPayload().get("fun"));

        // Test with an successful response
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_MEMINFO_RESPONSE)));

        Map<String, Result<Map<String, Map<String, Object>>>> response =
                call.callSync(client, new MinionList("minion"), AUTH).toCompletableFuture().join();

        assertNotNull(response.get("minion"));

        Map<String, Map<String, Object>> minion = response.get("minion").result().get();
        assertNotNull(minion);

        assertEquals(35, minion.size());

        assertEquals("657292", minion.get("Active(file)").get("value"));
        assertEquals("0", minion.get("WritebackTmp").get("value"));
        assertEquals("0", minion.get("SwapTotal").get("value"));
        assertEquals("194748", minion.get("Active(anon)").get("value"));
        assertEquals("0", minion.get("SwapFree").get("value"));
        assertEquals("1080", minion.get("KernelStack").get("value"));
        assertEquals("1572220", minion.get("MemFree").get("value"));
        assertEquals("408956", minion.get("Committed_AS").get("value"));
        assertEquals("0", minion.get("NFS_Unstable").get("value"));
        assertEquals("34359717488", minion.get("VmallocChunk").get("value"));
        assertEquals("0", minion.get("Writeback").get("value"));
        assertEquals("1132228", minion.get("Inactive(file)").get("value"));
        assertEquals("3845716", minion.get("MemTotal").get("value"));
        assertEquals("18752", minion.get("VmallocUsed").get("value"));
        assertEquals("194800", minion.get("AnonPages").get("value"));
        assertEquals("852040", minion.get("Active").get("value"));
        assertEquals("16", minion.get("Inactive(anon)").get("value"));
        assertEquals("1922856", minion.get("CommitLimit").get("value"));
        assertEquals("1550520", minion.get("Cached").get("value"));
        assertEquals("0", minion.get("SwapCached").get("value"));
        assertEquals("34359738367", minion.get("VmallocTotal").get("value"));
        assertEquals("56", minion.get("Shmem").get("value"));
        assertEquals("29228", minion.get("Mapped").get("value"));
        assertEquals("8364", minion.get("SUnreclaim").get("value"));
        assertEquals("0", minion.get("Unevictable").get("value"));
        assertEquals("256552", minion.get("SReclaimable").get("value"));
        assertEquals("0", minion.get("Mlocked").get("value"));
        assertEquals("0", minion.get("DirectMap2M").get("value"));
        assertEquals("0", minion.get("Bounce").get("value"));
        assertEquals("1132244", minion.get("Inactive").get("value"));
        assertEquals("3968", minion.get("PageTables").get("value"));
        assertEquals("3940352", minion.get("DirectMap4k").get("value"));
        assertEquals("264916", minion.get("Slab").get("value"));
        assertEquals("239048", minion.get("Buffers").get("value"));
        assertEquals("44", minion.get("Dirty").get("value"));
    }

    @Test
    public final void testLoadavg() {
        // First we get the call to use in the tests
        LocalCall<Map<String, Double>> call = Status.loadavg();
        assertEquals("status.loadavg", call.getPayload().get("fun"));

        // Test with an successful response
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_LOADAVG_RESPONSE)));

        Map<String, Result<Map<String, Double>>> response =
                call.callSync(client, new MinionList("minion"), AUTH).toCompletableFuture().join();

        assertNotNull(response.get("minion"));
        Map<String, Double> minion = response.get("minion").result().get();
        assertNotNull(minion);

        assertEquals(3, minion.size());
        assertEquals(Double.valueOf(0.22), minion.get("15-min"));
        assertEquals(Double.valueOf(0.1), minion.get("5-min"));
        assertEquals(Double.valueOf(0.16), minion.get("1-min"));
    }

    @Test
    public final void testDiskusage() {
        // First we get the call to use in the tests
        LocalCall<Map<String, Map<String, Long>>> call = Status.diskusage();
        assertEquals("status.diskusage", call.getPayload().get("fun"));

        // Test with an successful response
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_DISKUSAGE_RESPONSE)));

        Map<String, Result<Map<String, Map<String, Long>>>> response =
                call.callSync(client, new MinionList("minion"), AUTH).toCompletableFuture().join();

        assertNotNull(response.get("minion"));
        Map<String, Map<String, Long>> minion = response.get("minion").result().get();
        assertNotNull(minion);

        Map<String, Long> root = minion.get("/");
        assertEquals(2, root.size());
        assertEquals(Long.valueOf(2603425792L), root.get("available"));
        assertEquals(Long.valueOf(8562601984L), root.get("total"));
    }

    @Test
    public final void testDiskstats() {
        // First we get the call to use in the tests
        LocalCall<Map<String, Map<String, Object>>> call = Status.diskstats();
        assertEquals("status.diskstats", call.getPayload().get("fun"));

        // Test with an successful response
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_DISKSTATS_RESPONSE)));

        Map<String, Result<Map<String, Map<String, Object>>>> response =
                call.callSync(client, new MinionList("minion"), AUTH).toCompletableFuture().join();

        assertNotNull(response.get("minion"));
        Map<String, Map<String, Object>> minion = response.get("minion").result().get();
        assertNotNull(minion);

        Map<String, Object> disk = minion.get("xvda1");
        assertEquals(14, disk.size());
        assertEquals(2.26472296E8, disk.get("ms_spent_in_io"));
        assertEquals(2759532.0, disk.get("sectors_read"));
        assertEquals(1.80543231E8, disk.get("weighted_ms_spent_in_io"));
        assertEquals(4.43800912E8, disk.get("writes_completed"));
        assertEquals(202.0, disk.get("major"));
        assertEquals(2.5523542776E10, disk.get("sectors_written"));
        assertEquals(1.80175149E8, disk.get("ms_spent_writing"));
        assertEquals(4.51251256E8, disk.get("writes_merged"));
        assertEquals(479890.0, disk.get("ms_spent_reading"));
        assertEquals(0.0, disk.get("io_in_progress"));
        assertEquals(117.0, disk.get("reads_merged"));
        assertEquals("xvda1", disk.get("device"));
        assertEquals(92229.0, disk.get("reads_issued"));
        assertEquals(1.0, disk.get("minor"));
    }

    @Test
    public final void testUptime() {
        // First we get the call to use in the tests
        LocalCall<Map<String, Object>> call = Status.uptime();
        assertEquals("status.uptime", call.getPayload().get("fun"));

        // Test with an successful response
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_UPTIME_RESPONSE)));

        Map<String, Result<Map<String, Object>>> response = call.callSync(client,
                new MinionList("minion"), AUTH).toCompletableFuture().join();

        assertNotNull(response.get("minion"));
        Map<String, Object> minion = response.get("minion").result().get();
        assertNotNull(minion);
        assertEquals(6, minion.size());
        assertEquals(1.0, minion.get("users"));
        assertEquals(1.7906710E7, minion.get("seconds"));
        assertEquals(1.464613379E9, minion.get("since_t"));
        assertEquals(207.0, minion.get("days"));
        assertEquals("2016-05-30T13:02:59.145573", minion.get("since_iso"));
        assertEquals("6:5", minion.get("time"));
    }
}
