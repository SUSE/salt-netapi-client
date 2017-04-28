package com.suse.salt.netapi.calls.modules;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.HttpURLConnection;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.suse.salt.netapi.calls.LocalCall;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.datatypes.target.MinionList;
import com.suse.salt.netapi.exception.SaltException;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.utils.ClientUtils;

/**
 * Schedule module unit tests.
 */
public class ScheduleTest {

    private static final int MOCK_HTTP_PORT = 8888;

    static final String JSON_LIST_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/schedule/list.json"));

    private SaltClient client;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(MOCK_HTTP_PORT);

    @Before
    public void init() {
        URI uri = URI.create("http://localhost:" + MOCK_HTTP_PORT);
        client = new SaltClient(uri);
    }

    @Test
    public final void testList() throws SaltException {
        // First we get the call to use in the tests
        LocalCall<Map<String, Map<String, Object>>> call = Schedule.list(true);
        assertEquals("schedule.list", call.getPayload().get("fun"));

        // Test with an successful response
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_LIST_RESPONSE)));

        Map<String, Result<Map<String, Map<String, Object>>>> response =
                call.callSync(client, new MinionList("minion"));

        assertNotNull(response.get("minion"));

        Map<String, Map<String, Object>> minion = response.get("minion").result().get();
        assertNotNull(minion);

        Map<String, Object> job = minion.get("__mine_interval");

        assertEquals(7, job.size());

        assertEquals("__mine_interval", job.get("name"));
        assertEquals("mine.update", job.get("function"));
        assertEquals(true, job.get("enabled"));
        assertEquals(false, job.get("return_job"));
        assertEquals(true, job.get("jid_include"));
        assertEquals(2.0, job.get("maxrunning"));
        assertEquals(60.0, job.get("minutes"));
    }

    /**
     * Salt cannot parse schedule dates that include a fraction of seconds. This test
     * should make sure that a fraction of seconds is cut off before the request is sent.
     */
    @Test
    @SuppressWarnings("unchecked")
    public final void testAddDateTimeFormat() throws SaltException {
        LocalDateTime scheduleDate = LocalDateTime.of(2017, 12, 24, 15, 30, 12, 345000000);
        assertEquals("2017-12-24T15:30:12.345",
                scheduleDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        LocalCall<com.suse.salt.netapi.calls.modules.Schedule.Result> call =
                Schedule.add("Test Job", com.suse.salt.netapi.calls.modules.Test.ping(),
                        scheduleDate, Collections.EMPTY_MAP);
        Map<String, Object> kwarg = (Map<String, Object>) call.getPayload().get("kwarg");
        assertEquals("2017-12-24T15:30:12", kwarg.get("once"));
    }
}
