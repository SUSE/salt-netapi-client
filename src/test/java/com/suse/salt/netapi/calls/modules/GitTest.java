package com.suse.salt.netapi.calls.modules;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.suse.salt.netapi.calls.LocalCall;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.client.impl.HttpAsyncClientImpl;
import com.suse.salt.netapi.datatypes.AuthMethod;
import com.suse.salt.netapi.datatypes.Token;
import com.suse.salt.netapi.datatypes.target.MinionList;
import com.suse.salt.netapi.results.GitResult;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.utils.ClientUtils;
import com.suse.salt.netapi.utils.TestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Git unit tests.
 */
public class GitTest {

    private static final int MOCK_HTTP_PORT = 8888;

    static final String JSON_STATUS_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/git/git_status.json"));

    static final String JSON_ADD_SUCCESS_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/git/git_add_success.json"));

    static final String JSON_ADD_ERROR_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/git/git_add_error.json"));

    static final String JSON_COMMIT_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/git/git_commit.json"));

    static final String JSON_BRANCH_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/git/git_branch.json"));

    static final AuthMethod AUTH = new AuthMethod(new Token());

    private SaltClient client;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(MOCK_HTTP_PORT);

    @Before
    public void init() {
        URI uri = URI.create("http://localhost:" + Integer.toString(MOCK_HTTP_PORT));
        client = new SaltClient(uri, new HttpAsyncClientImpl(TestUtils.defaultClient()));
    }

    @Test
    public void testStatus() {
        // First we get the call to use in the tests
        LocalCall<GitResult> call = Git.status("/dev", Optional.empty());
        assertEquals("git.status", call.getPayload().get("fun"));

        // Test with a successful response
        mockOkResponseWith(JSON_STATUS_RESPONSE);

        Map<String, Result<GitResult>> response =
                call.callSync(client, new MinionList("myminion"), AUTH)
                        .toCompletableFuture().join();

        assertNotNull(response.get("myminion"));

        GitResult output = response.get("myminion").result().get();
        assertTrue(output.getModified().size() == 2);
        assertEquals("src/main/java/com/suse/salt/netapi/client/SaltClient.java," +
                "src/test/java/com/suse/salt/netapi/examples/Calls.java",
                output.getModified().stream().collect(Collectors.joining(",")));
    }

    @Test
    public void testAddSuccess() {
        // First we get the call to use in the tests
        LocalCall<String> call = Git.add("/dev", "add_test_file.txt", "", "", Optional.empty());
        assertEquals("git.add", call.getPayload().get("fun"));

        // Test with a successful response
        mockOkResponseWith(JSON_ADD_SUCCESS_RESPONSE);

        Map<String, Result<String>> response =
                call.callSync(client, new MinionList("myminion"), AUTH)
                        .toCompletableFuture().join();

        assertNotNull(response.get("myminion"));

        String output = response.get("myminion").result().get();
        assertEquals("add 'add_test_file.txt'", output);
    }

    @Test
    public void testAddError() {
        // First we get the call to use in the tests
        LocalCall<String> call = Git.add("/dev", "add_test_file.txt", "", "", Optional.empty());
        assertEquals("git.add", call.getPayload().get("fun"));

        // Test with a successful response
        mockOkResponseWith(JSON_ADD_ERROR_RESPONSE);

        Map<String, Result<String>> response =
                call.callSync(client, new MinionList("myminion"), AUTH)
                        .toCompletableFuture().join();

        assertNotNull(response.get("myminion"));

        String output = response.get("myminion").result().get();
        assertEquals("ERROR: Command 'git add --verbose -- add_test_file.txt' failed: fatal: " +
                "pathspec 'add_test_file.txt' did not match any files",
                output);
    }

    @Test
    public void testCommit() {
        // First we get the call to use in the tests
        LocalCall<String> call = Git.commit("/dev", "Test commit message", "", "",
                Optional.empty(), Optional.empty());
        assertEquals("git.commit", call.getPayload().get("fun"));

        // Test with a successful response
        mockOkResponseWith(JSON_COMMIT_RESPONSE);

        Map<String, Result<String>> response =
                call.callSync(client, new MinionList("myminion"), AUTH)
                        .toCompletableFuture().join();

        assertNotNull(response.get("myminion"));

        String output = response.get("myminion").result().get();
        assertEquals("[master a5d60b640] Test commit\n 1 file changed, 1 insertion(+)\n create mode " +
                "100644 add_test_file.txt",
                output);
    }

    @Test
    public void testBranch() {
        // First we get the call to use in the tests
        LocalCall<Boolean> call = Git.branch("/dev", "feature/git_module", "", "", Optional.empty());
        assertEquals("git.branch", call.getPayload().get("fun"));

        // Test with a successful response
        mockOkResponseWith(JSON_BRANCH_RESPONSE);

        Map<String, Result<Boolean>> response =
                call.callSync(client, new MinionList("myminion"), AUTH)
                        .toCompletableFuture().join();

        assertNotNull(response.get("myminion"));

        Boolean output = response.get("myminion").result().get();
        assertEquals(true, output);
    }

    private static void mockOkResponseWith(String json) {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                        .withStatus(HttpURLConnection.HTTP_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBody(json)));
    }
}
