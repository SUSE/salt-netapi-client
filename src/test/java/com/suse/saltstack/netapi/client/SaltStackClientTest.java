package com.suse.saltstack.netapi.client;

import com.suse.saltstack.netapi.exception.SaltStackException;

import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.verify.VerificationTimes;
import org.mockserver.model.Header;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;


public class SaltStackClientTest {

    private static final int MOCK_HTTP_PORT = 8888;
    private static final String AUTH_TOKEN = "7f966bcf66ffc36ca168b7d330fcb0321645a802";
    private static final String JSON_LOGIN_REQUEST = "{\"username\":\"user\"," +
            "\"password\":\"pass\",\"eauth\":\"auto\"}";
    private static final String JSON_LOGIN_RESPONSE = "{\"return\": [{\"perms\": [\".*\"" +
            "],\"start\": 1422790143.013817, \"token\": \"" + AUTH_TOKEN + "\", " +
            " \"expire\": 1422833343.013818, \"user\": \"user\", \"eauth\": \"auto\"}]}";



    private SaltStackClient client;
    private MockServerClient mock;  // populated by mockServerRule

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(MOCK_HTTP_PORT, this);


    @Before
    public void init() throws SaltStackException {
        client = new SaltStackClient(
                "http://localhost:" + Integer.toString(MOCK_HTTP_PORT));

    }

    @Test
    public void LoginSendsCorrectRequest() {
        try {
            client.login("user", "pass");
        } catch (SaltStackException s) {}

        mock.verify(
            request()
                .withMethod("POST")
                .withPath("/login")
                .withBody(JSON_LOGIN_REQUEST)
                .withHeaders(
                    new Header("Content-Type", "application/json"),
                    new Header("Accept", "application/json")
                ),
            VerificationTimes.exactly(1)
        );
    }

    @Test
    public void SuccessfulLoginHandled() {
        SaltStackToken authToken = null;
        mock
            .when(
                request()
                    .withMethod("POST")
                    .withPath("/login")
            )
            .respond(
                response()
                    .withStatusCode(200)
                    .withHeaders(new Header("Content-Type", "application/json"))
                    .withBody(JSON_LOGIN_RESPONSE)
            );

        try {
            authToken = client.login("user", "pass");
        } catch (SaltStackException s) {
            fail ("SaltStackException thrown with message: " + s.getMessage());
        }

        assertNotNull(authToken);
        assertEquals(AUTH_TOKEN, authToken.getToken());

    }

}
