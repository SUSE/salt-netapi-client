package com.suse.salt.netapi.client;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.suse.salt.netapi.AuthModule;
import com.suse.salt.netapi.calls.modules.Minion;
import com.suse.salt.netapi.datatypes.Token;
import com.suse.salt.netapi.datatypes.target.Glob;
import com.suse.salt.netapi.datatypes.target.Target;
import com.suse.salt.netapi.exception.SaltException;
import com.suse.salt.netapi.exception.SaltUserUnauthorizedException;
import com.suse.salt.netapi.results.Result;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionException;

/**
 * SaltStack API unit tests against a running SaltStack Master with NetAPI
 * module enabled. In order to pass these tests you MUST have a salt-master
 * NetAPI enabled listening at: http://SALT_NETAPI_SERVER:SALT_NETAPI_PASSWORD/
 * <p>
 * If you are looking for a quick way to bring up a SaltStack Master with NETAPI
 * enabled locally, take a look at .travis.yml
 */
public class SaltClientDockerTest {

    private static final String SALT_NETAPI_SERVER = "http://localhost";
    private static final int SALT_NETAPI_PORT = 8000;
    private static final String SALT_NETAPI_USER = "saltdev";
    private static final String SALT_NETAPI_PASSWORD = "saltdev";
    private static final AuthModule SALT_NETAPI_AUTH = AuthModule.PAM;

    private SaltClient client;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void init() throws SaltException {
        URI uri = URI.create(SALT_NETAPI_SERVER + ":" + SALT_NETAPI_PORT);
        client = new SaltClient(uri);
    }

    @Test
    public void testLoginOk() throws Exception {
        Token token = client.login(SALT_NETAPI_USER, SALT_NETAPI_PASSWORD, SALT_NETAPI_AUTH)
                .toCompletableFuture().join();
        assertNotNull(token);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tokenStart = LocalDateTime.ofInstant(token.getStart().toInstant(),
                ZoneId.systemDefault());
        LocalDateTime tokenExpiration = LocalDateTime
                .ofInstant(token.getExpire().toInstant(), ZoneId.systemDefault());

        assertTrue(tokenStart.isBefore(now));
        assertTrue(tokenExpiration.isAfter(now));
    }

    @Test
    public void testLoginFailure() throws Exception {
        exception.expect(CompletionException.class);
        exception.expectCause(instanceOf(SaltUserUnauthorizedException.class));
        client.login("user", "pass", AuthModule.DJANGO).toCompletableFuture().join();
    }

    @Test
    public void testLoginAsyncOk() throws Exception {
        Runnable cleanup = () -> {
            try {
                client.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        client.login(SALT_NETAPI_USER, SALT_NETAPI_PASSWORD, SALT_NETAPI_AUTH)
                .thenAccept(token -> {
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime tokenStart = LocalDateTime.ofInstant(token.getStart().toInstant(),
                            ZoneId.systemDefault());
                    LocalDateTime tokenExpiration = LocalDateTime
                            .ofInstant(token.getExpire().toInstant(), ZoneId.systemDefault());

                    assertTrue(tokenStart.isBefore(now));
                    assertTrue(tokenExpiration.isAfter(now));
                })
                .thenRun(cleanup);
    }

    @Test
    public void testLoginAsyncFailure() throws Exception {
        Runnable cleanup = () -> {
            try {
                client.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        client.login(SALT_NETAPI_USER, SALT_NETAPI_PASSWORD, SALT_NETAPI_AUTH)
                .thenAccept(token -> {
                    assertNull(token);
                })
                .thenRun(cleanup);
    }

    @Test
    public void testGetMinions() throws Exception {
        SaltClient client = new SaltClient(URI.create(SALT_NETAPI_SERVER + ":" + SALT_NETAPI_PORT));

        Target<String> globTarget = new Glob("*");
        Map<String, Result<Map<String, Set<String>>>> minions = Minion.list().callSync(
                client, globTarget, SALT_NETAPI_USER, SALT_NETAPI_PASSWORD, SALT_NETAPI_AUTH)
                .toCompletableFuture().join();

        assertNotNull(minions);
        assertEquals(2, minions.size());
    }

    @Test
    public void testTestVersions() throws Exception {
        SaltClient client = new SaltClient(URI.create(SALT_NETAPI_SERVER + ":" + SALT_NETAPI_PORT));

        Target<String> globTarget = new Glob("*");
        Map<String, Result<com.suse.salt.netapi.calls.modules.Test.VersionInformation>> results =
                com.suse.salt.netapi.calls.modules.Test.versionsInformation().callSync(
                        client, globTarget, SALT_NETAPI_USER, SALT_NETAPI_PASSWORD, SALT_NETAPI_AUTH)
                        .toCompletableFuture().join();

        assertNotNull(results);
        assertEquals(2, results.size());
        results.forEach((minion, result) -> {
            assertEquals("2018.3.2", result.result().get().getSalt().get("Salt"));
        });
    }
}
