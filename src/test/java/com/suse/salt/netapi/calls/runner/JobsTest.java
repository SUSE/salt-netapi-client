package com.suse.salt.netapi.calls.runner;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.suse.salt.netapi.calls.modules.SaltUtilTest;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.client.impl.HttpAsyncClientImpl;
import com.suse.salt.netapi.datatypes.AuthMethod;
import com.suse.salt.netapi.datatypes.Token;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.utils.ClientUtils;
import com.suse.salt.netapi.utils.TestUtils;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Optional;

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

    static final AuthMethod AUTH = new AuthMethod(new Token());

    private SaltClient client;

    private CloseableHttpAsyncClient closeableHttpAsyncClient;

    @Before
    public void init() {
        URI uri = URI.create("http://localhost:" + MOCK_HTTP_PORT);
        closeableHttpAsyncClient = TestUtils.defaultClient();
        client = new SaltClient(uri, new HttpAsyncClientImpl(closeableHttpAsyncClient));
    }

    @After
    public void cleanup() throws IOException {
        closeableHttpAsyncClient.close();
    }

    @Test
    public void testListJob() {
    	stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_LIST_JOB_RESPONSE)));

    	Result<Jobs.Info> result = Jobs.listJob("111").callSync(client, AUTH).toCompletableFuture().join();

        assertTrue(result.result().isPresent());
        assertEquals(Optional.empty(), result.error());

    }

    @Test
    public void testListJobInvalidResponse() {
    	stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_LIST_JOB_UNPARSABLE_DATE_RESPONSE)));

    	Result<Jobs.Info> result = Jobs.listJob("111").callSync(client, AUTH).toCompletableFuture().join();

    	assertEquals(Optional.empty(), result.result());
    	assertTrue(result.error().isPresent());
        assertThat(result.error().get().toString(),
        		       CoreMatchers.containsString("java.text.ParseException:"));
    }

    @Test
    public void testResponseWithException() {
    	stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_SALT_EXCEPTION_RESPONSE)));

    	Result<Jobs.Info> result = Jobs.listJob("111").callSync(client, AUTH).toCompletableFuture().join();

    	assertEquals(Optional.empty(), result.result());
    	assertTrue(result.error().isPresent());
        assertThat(result.error().get().toString(),
        		       CoreMatchers.containsString("unexpected keyword argument"
        		    		                       + " 'kwargs'"));

    }
}
