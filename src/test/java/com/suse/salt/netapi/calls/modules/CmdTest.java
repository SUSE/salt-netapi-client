package com.suse.salt.netapi.calls.modules;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.suse.salt.netapi.calls.LocalCall;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.datatypes.target.MinionList;
import com.suse.salt.netapi.exception.SaltException;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.utils.ClientUtils;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

/**
 * Cmd unit tests.
 */
public class CmdTest {
    private static final int MOCK_HTTP_PORT = 8888;

    static final String JSON_RUN_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/cmd/uptime.json"));

    static final String JSON_HAS_EXEC_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/cmd/has_exec.json"));

    static final String JSON_EXEC_CODE_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/cmd/exec_code.json"));

    static final String JSON_EXEC_CODE_ALL_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/cmd/exec_code_all.json"));

    private SaltClient client;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(MOCK_HTTP_PORT);

    @Before
    public void init() {
        URI uri = URI.create("http://localhost:" + MOCK_HTTP_PORT);
        client = new SaltClient(uri);
    }

    @Test
    public void testCmdUptime() throws SaltException {
        // First we get the call to use in the tests
        LocalCall<String> call = Cmd.run("uptime");
        assertEquals("cmd.run", call.getPayload().get("fun"));

        // Test with an successful response
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_RUN_RESPONSE)));

        Map<String, Result<String>> response =
                call.callSync(client, new MinionList("minion"));

        assertNotNull(response.get("minion"));

        String output = response.get("minion").result().get();
        assertEquals(" 16:51:23 up 22 min,  0 users,  load average: 0.00, 0.02, 0.09",
                output);
    }

    @Test
    public void testCmdHasExec() throws SaltException {
        // First we get the call to use in the tests
        LocalCall<Boolean> call = Cmd.hasExec("uptime");
        assertEquals("cmd.has_exec", call.getPayload().get("fun"));

        // Test with an successful response
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_HAS_EXEC_RESPONSE)));

        Map<String, Result<Boolean>> response =
                call.callSync(client, new MinionList("minion"));

        assertNotNull(response.get("minion"));

        Boolean output = response.get("minion").result().get();
        assert(output);
    }

    @Test
    public void testCmdExecCode() throws SaltException {
        // First we get the call to use in the tests
        LocalCall<String> call =
                Cmd.execCode("python", "import sys; print sys.version");
        assertEquals("cmd.exec_code", call.getPayload().get("fun"));

        // Test with an successful response
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_EXEC_CODE_RESPONSE)));

        Map<String, Result<String>> response =
                call.callSync(client, new MinionList("minion"));

        assertNotNull(response.get("minion"));

        String output = response.get("minion").result().get();
        assertEquals("2.6.6 (r266:84292, Jul 23 2015, 15:22:56) "
                + "[GCC 4.4.7 20120313 (Red Hat 4.4.7-11)]", output);
    }

    @Test
    public void testCmdExecCodeAll() throws SaltException {
        // First we get the call to use in the tests
        LocalCall<Map<String, Object>> call =
                Cmd.execCodeAll("python", "import sys; print sys.version");
        assertEquals("cmd.exec_code_all", call.getPayload().get("fun"));

        // Test with an successful response
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_EXEC_CODE_ALL_RESPONSE)));

        Map<String, Result<Map<String, Object>>> response =
                call.callSync(client, new MinionList("minion"));

        assertNotNull(response.get("minion"));
        Map<String, Object> output = response.get("minion").result().get();
        assertEquals(4, output.size());

        assertEquals(27299.0, output.get("pid"));
        assertEquals(0.0, output.get("retcode"));
        assertEquals("", output.get("stderr"));
        assertEquals("2.6.6 (r266:84292, Jul 23 2015, 15:22:56) "
                + "[GCC 4.4.7 20120313 (Red Hat 4.4.7-11)]", output.get("stdout"));
    }
}
