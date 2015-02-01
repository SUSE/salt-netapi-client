package com.suse.saltstack.netapi.client;

import com.suse.saltstack.netapi.exception.SaltStackException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * SaltStack API unit tests.
 */
public class SaltStackClientTest {

    @Test
    public void testLoginOk() throws SaltStackException {
        final String MOCK_JSON = "{\"return\": [{\"perms\": [\".*\"], \"start\": 1422803163.765152, " +
                "\"token\": \"2fea67bb673e012f11ca7cad0d1079ccf1decaa2\", \"expire\": 1422846363.765152, " +
                "\"user\": \"salt\", \"eauth\": \"pam\"}]}";

        SaltStackMockConnectionFactory mockConnectionFactory =
                new SaltStackMockConnectionFactory(MOCK_JSON);
        SaltStackClient client = new SaltStackClient("http://www.suse.com", mockConnectionFactory);
        SaltStackToken token = client.login("salt", "salt");
        assertEquals("Token mismatch", token.getToken(), "2fea67bb673e012f11ca7cad0d1079ccf1decaa2");
        assertEquals("EAuth mismatch", token.getEauth(), "pam");
        assertEquals("User mismatch", token.getUser(), "salt");
    }

    @Test(expected = SaltStackException.class)
    public void testLoginFailure() throws SaltStackException {
        SaltStackMockConnectionFactory mockConnectionFactory =
                new SaltStackMockConnectionFactory(new SaltStackException("HTTP 401 Unauthorized"));
        SaltStackClient client = new SaltStackClient("http://www.suse.com", mockConnectionFactory);
        client.login("user", "pass");
    }
}
