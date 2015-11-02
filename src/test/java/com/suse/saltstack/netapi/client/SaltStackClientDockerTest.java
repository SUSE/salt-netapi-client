package com.suse.saltstack.netapi.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.suse.saltstack.netapi.AuthModule;
import com.suse.saltstack.netapi.datatypes.Token;
import com.suse.saltstack.netapi.exception.SaltStackException;
import com.suse.saltstack.netapi.exception.SaltUserUnauthorizedException;

/**
 * SaltStack API unit tests against a running SaltStack Master with NetAPI module enabled.
 * In order to pass these tests you MUST have a salt-master NetAPI enabled listening at:
 * http://SALT_NETAPI_SERVER:SALT_NETAPI_PASSWORD/
 *
 * Please read docker/README.md to launch a Docker container in order to pass these tests.
 */
public class SaltStackClientDockerTest {

    private static final String SALT_NETAPI_SERVER = "http://localhost";
    private static final int SALT_NETAPI_PORT = 8000;
    private static final String SALT_NETAPI_USER = "saltdev";
    private static final String SALT_NETAPI_PASSWORD = "saltdev";
    private static final AuthModule SALT_NETAPI_AUTH = AuthModule.PAM;

    private SaltStackClient client;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void init() throws SaltStackException {
        URI uri = URI.create(SALT_NETAPI_SERVER + ":" + SALT_NETAPI_PORT);
        client = new SaltStackClient(uri);
    }

    @Test
    public void testLoginOk() throws Exception {
        Token token = client.login(SALT_NETAPI_USER,
                SALT_NETAPI_PASSWORD, SALT_NETAPI_AUTH);
        assertNotNull(token);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tokenStart = LocalDateTime.ofInstant(
                token.getStart().toInstant(),
                ZoneId.systemDefault());
        LocalDateTime tokenExpiration = LocalDateTime.ofInstant(
                token.getExpire().toInstant(),
                ZoneId.systemDefault());

        assertTrue(tokenStart.isBefore(now));
        assertTrue(tokenExpiration.isAfter(now));
    }

    @Test(expected = SaltUserUnauthorizedException.class)
    public void testLoginFailure() throws Exception {
        client.login("user", "pass", AuthModule.DJANGO);
    }

    @Test
    public void testLoginAsyncOk() throws Exception {
        Future<Token> futureToken = client.loginAsync(
                SALT_NETAPI_USER, SALT_NETAPI_PASSWORD, SALT_NETAPI_AUTH);
        Token token = futureToken.get();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tokenStart = LocalDateTime.ofInstant(
                token.getStart().toInstant(),
                ZoneId.systemDefault());
        LocalDateTime tokenExpiration = LocalDateTime.ofInstant(
                token.getExpire().toInstant(),
                ZoneId.systemDefault());

        assertTrue(tokenStart.isBefore(now));
        assertTrue(tokenExpiration.isAfter(now));
    }

    @Test(expected = ExecutionException.class)
    public void testLoginAsyncFailure() throws Exception {
        Future<Token> futureToken = client.loginAsync("user", "pass", AuthModule.DJANGO);
        Token token = futureToken.get();
        assertNull(token);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetMinions() throws Exception {
        client.login(SALT_NETAPI_USER,
                SALT_NETAPI_PASSWORD, SALT_NETAPI_AUTH);
        Map<String, Map<String, Object>> minions = client.getMinions();

        assertNotNull(minions);
        assertEquals(2, minions.size());

        assertTrue(minions.containsKey("minion1"));
        assertTrue(minions.containsKey("minion2"));

        Map<String, Object> minion1 = minions.get("minion1");
        assertEquals("Linux", minion1.get("kernel"));
        assertEquals("Ubuntu", minion1.get("lsb_distrib_id"));
        assertEquals("14.04", minion1.get("lsb_distrib_release"));

        assertTrue(minion1.get("saltversioninfo") instanceof List);
        List<String> saltVersionInfo = (List<String>) minion1.get("saltversioninfo");
        assertEquals(2015.0, saltVersionInfo.get(0));
        assertEquals(8.0, saltVersionInfo.get(1));
        assertEquals(1.0, saltVersionInfo.get(2));
        assertEquals(0.0, saltVersionInfo.get(3));

        Map<String, Object> minion2 = minions.get("minion2");
        assertEquals("Linux", minion2.get("kernel"));
        assertEquals("Ubuntu", minion2.get("lsb_distrib_id"));
        assertEquals("14.04", minion2.get("lsb_distrib_release"));

        assertTrue(minion1.get("saltversioninfo") instanceof List);
        saltVersionInfo = (List<String>) minion2.get("saltversioninfo");
        assertEquals(2015.0, saltVersionInfo.get(0));
        assertEquals(8.0, saltVersionInfo.get(1));
        assertEquals(1.0, saltVersionInfo.get(2));
        assertEquals(0.0, saltVersionInfo.get(3));
    }
}
