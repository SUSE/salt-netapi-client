package com.suse.salt.netapi.calls.runner;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Optional;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.suse.salt.netapi.calls.modules.SaltUtilTest;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.exception.SaltException;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.utils.ClientUtils;

/**
 * Tests for the runner Jobs module.
 */
public class JobsTest {

    private static final int MOCK_HTTP_PORT = 8888;

    static final String JSON_LIST_JOB_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/runner/list_job_response.json"));
    static final String JSON_LIST_JOB_UNPARSABLE_DATE_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.
            getResourceAsStream("/runner/list_job_unparseable_date_response.json"));
    static final String JSON_SALT_EXCEPTION_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/runner/salt_exception_response.json"));

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(MOCK_HTTP_PORT);

    private SaltClient client;

    @Before
    public void init() {
        URI uri = URI.create("http://localhost:" + Integer.toString(MOCK_HTTP_PORT));
        client = new SaltClient(uri);
    }

    @Test
    public void testListJob() throws SaltException {
    	stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_LIST_JOB_RESPONSE)));

    	Result<Jobs.Info> result = Jobs.listJob("111").callSync(client);

        assertTrue(result.result().isPresent());
        assertEquals(Optional.empty(), result.error());

    }

    @Test
    public void testListJobInvalidResponse() throws SaltException {
    	stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_LIST_JOB_UNPARSABLE_DATE_RESPONSE)));

    	Result<Jobs.Info> result = Jobs.listJob("111").callSync(client);

    	assertEquals(Optional.empty(), result.result());
    	assertTrue(result.error().isPresent());
        assertThat(result.error().get().toString(),
        		       CoreMatchers.containsString("java.text.ParseException:"));
    }

    @Test
    public void testResponseWithException() throws SaltException {
    	stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_SALT_EXCEPTION_RESPONSE)));

    	Result<Jobs.Info> result = Jobs.listJob("111").callSync(client);

    	assertEquals(Optional.empty(), result.result());
    	assertTrue(result.error().isPresent());
        assertThat(result.error().get().toString(),
        		       CoreMatchers.containsString("unexpected keyword argument"
        		    		                       + " 'kwargs'"));

    }
}
