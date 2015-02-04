package com.suse.saltstack.netapi.client;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.suse.saltstack.netapi.exception.SaltStackException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

/**
 * SaltStack API unit tests.
 */
public class SaltStackClientTest {
    private static final int MOCK_HTTP_PORT = 8888;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(MOCK_HTTP_PORT);

    private SaltStackClient client;

    @Before
    public void init() throws SaltStackException {
        client = new SaltStackClient(
                "http://localhost:" + Integer.toString(MOCK_HTTP_PORT),
                new SaltStackHttpClientConnectionFactory());
    }

    @Test
    public void testLoginOk() throws SaltStackException {
        final String JSON_LOGIN_REQUEST = "{\"username\":\"user\"," +
                "\"password\":\"pass\",\"eauth\":\"pam\"}";
        final String JSON_LOGIN_RESPONSE = "{\"return\": [{\"perms\": [\".*\"], " +
                "\"start\": 1422803163.765152, " +
                "\"token\": \"2fea67bb673e012f11ca7cad0d1079ccf1decaa2\", " +
                "\"expire\": 1422846363.765152, " +
                "\"user\": \"user\", \"eauth\": \"pam\"}]}";

        stubFor(post(urlEqualTo("/login"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalTo(JSON_LOGIN_REQUEST))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(JSON_LOGIN_RESPONSE)));

        SaltStackToken token = client.login("user", "pass", SaltStackAPIConstants.LOGIN_EAUTH_PAM);
        assertEquals("Token mismatch", token.getToken(), "2fea67bb673e012f11ca7cad0d1079ccf1decaa2");
        assertEquals("EAuth mismatch", token.getEauth(), "pam");
        assertEquals("User mismatch", token.getUser(), "user");
    }

    @Test(expected = SaltStackException.class)
    public void testLoginFailure() throws SaltStackException {
        stubFor(post(urlEqualTo("/login"))
                .withHeader("Accept", equalTo("application/json"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(401)));
        client.login("user", "pass");
    }
}
