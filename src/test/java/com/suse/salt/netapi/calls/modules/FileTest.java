package com.suse.salt.netapi.calls.modules;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import com.suse.salt.netapi.calls.LocalCall;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.datatypes.target.MinionList;
import com.suse.salt.netapi.exception.SaltException;
import com.suse.salt.netapi.results.GenericSaltError;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.utils.ClientUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertArrayEquals;

/**
 * File module unit tests.
 */
public class FileTest {

    private static final int MOCK_HTTP_PORT = 8888;

    static final String JSON_NULL_RESPONSE = ClientUtils.streamToString(
            FileTest.class.getResourceAsStream(
            "/modules/file/null_response.json"));

    static final String JSON_TRUE_RESPONSE = ClientUtils.streamToString(
            FileTest.class.getResourceAsStream(
            "/modules/file/true_response.json"));

    static final String JSON_FALSE_RESPONSE = ClientUtils.streamToString(
            FileTest.class.getResourceAsStream(
            "/modules/file/false_response.json"));

    static final String JSON_FILENOTFOUND_RESPONSE = ClientUtils.streamToString(
            FileTest.class.getResourceAsStream(
            "/modules/file/filenotfound_response.json"));

    static final String JSON_FILENOTFOUND_EXCEPTION_RESPONSE = ClientUtils.streamToString(
            FileTest.class.getResourceAsStream(
            "/modules/file/filenotfound_exception_response.json"));

    static final String JSON_MODE_RESPONSE = ClientUtils.streamToString(
            FileTest.class.getResourceAsStream(
            "/modules/file/mode_response.json"));

    static final String JSON_COPY_EXCEPTION_RESPONSE = ClientUtils.streamToString(
            FileTest.class.getResourceAsStream(
            "/modules/file/copy_exception_response.json"));

    static final String JSON_MOVE_RESPONSE = ClientUtils.streamToString(
            FileTest.class.getResourceAsStream(
            "/modules/file/move_response.json"));

    static final String JSON_MOVE_EXCEPTION_RESPONSE = ClientUtils.streamToString(
            FileTest.class.getResourceAsStream(
            "/modules/file/move_exception_response.json"));

    static final String JSON_REMOVE_EXCEPTION_RESPONSE = ClientUtils.streamToString(
            FileTest.class.getResourceAsStream(
            "/modules/file/remove_exception_response.json"));

    static final String JSON_GET_HASH_RESPONSE = ClientUtils.streamToString(
            FileTest.class.getResourceAsStream(
            "/modules/file/get_hash_sha256_response.json"));

    static final String JSON_READDIR_RESPONSE = ClientUtils.streamToString(
            FileTest.class.getResourceAsStream(
            "/modules/file/readdir_response.json"));

    static final String JSON_READDIR_EXCEPTION_RESPONSE = ClientUtils.streamToString(
            FileTest.class.getResourceAsStream(
            "/modules/file/readdir_exception_response.json"));

    static final String JSON_RMDIR_EXCEPTION_RESPONSE = ClientUtils.streamToString(
            FileTest.class.getResourceAsStream(
            "/modules/file/rmdir_exception_response.json"));

    private SaltClient client;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(MOCK_HTTP_PORT);

    @Before
    public void init() {
        URI uri = URI.create("http://localhost:" + MOCK_HTTP_PORT);
        client = new SaltClient(uri);
    }

    @Test
    public final void testChown() throws SaltException {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_NULL_RESPONSE)));

        LocalCall<String> call = File.chown("/test", "testuser", "testgroup");
        assertEquals("file.chown", call.getPayload().get("fun"));

        Map<String, Result<String>> response = call.callSync(client,
                new MinionList("minion1"));
        assertEquals(new GenericSaltError(JsonNull.INSTANCE),
                response.get("minion1").error().get());

        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_FILENOTFOUND_RESPONSE)));

        response = call.callSync(client, new MinionList("minion1"));
        assertEquals("File not found", response.get("minion1").result().get());
    }

    @Test
    public final void testChmod() throws SaltException {
        stubFor(any(urlMatching("/")).willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_MODE_RESPONSE)));

        LocalCall<String> call = File.chmod("/test", "755");
        assertEquals("file.set_mode", call.getPayload().get("fun"));

        Map<String, Result<String>> response = call.callSync(client,
                new MinionList("minion1"));
        assertEquals("0755", response.get("minion1").result().get());

        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_FILENOTFOUND_EXCEPTION_RESPONSE)));

        response = call.callSync(client, new MinionList("minion1"));
        assertEquals("ERROR: /test: File not found",
                response.get("minion1").result().get());
    }

    @Test
    public final void testCopy() throws SaltException {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_TRUE_RESPONSE)));

        LocalCall<Boolean> call = File.copy("/test1", "/test2", false, false);
        assertEquals("file.copy", call.getPayload().get("fun"));

        Map<String, Result<Boolean>> response = call.callSync(client,
                new MinionList("minion1"));
        assertTrue(response.get("minion1").result().get());

        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_COPY_EXCEPTION_RESPONSE)));

        response = call.callSync(client, new MinionList("minion1"));
        String errorMessage = "ERROR: Could not copy /test1 to /test2";
        assertEquals(new GenericSaltError(new JsonPrimitive(errorMessage)),
                response.get("minion1").error().get());
    }

    @Test
    public final void testMove() throws SaltException {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_MOVE_RESPONSE)));

        LocalCall<File.Result> call = File.move("/test1", "/test2");
        assertEquals("file.move", call.getPayload().get("fun"));

        Map<String, Result<File.Result>> response = call.callSync(client,
                new MinionList("minion1"));
        File.Result result = response.get("minion1").result().get();
        assertTrue(result.getResult());
        assertEquals("'/test1' moved to '/test2'", result.getComment());

        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_MOVE_EXCEPTION_RESPONSE)));

        response = call.callSync(client, new MinionList("minion1"));
        String errorMessage = "ERROR: Unable to move '/test1' to '/test2': " +
                "[Errno 2] No such file or directory: '/test1'";
        assertEquals(new GenericSaltError(new JsonPrimitive(errorMessage)),
                response.get("minion1").error().get());
    }

    @Test
    public final void testRemove() throws SaltException {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_TRUE_RESPONSE)));

        LocalCall<Boolean> call = File.remove("/test");
        assertEquals("file.remove", call.getPayload().get("fun"));

        Map<String, Result<Boolean>> response = call.callSync(client,
                new MinionList("minion1"));
        assertTrue(response.get("minion1").result().get());

        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_FALSE_RESPONSE)));

        response = call.callSync(client, new MinionList("minion1"));
        assertFalse(response.get("minion1").result().get());

        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_REMOVE_EXCEPTION_RESPONSE)));

        response = call.callSync(client, new MinionList("minion1"));
        String errorMessage = "ERROR: Could not remove /test: " +
                "[Errno 2] No such file or directory: '/test'";
        assertEquals(new GenericSaltError(new JsonPrimitive(errorMessage)),
                response.get("minion1").error().get());
    }

    @Test
    public final void testGetHash() throws SaltException {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_GET_HASH_RESPONSE)));

        LocalCall<String> call = File.getHash("/test", HashType.SHA256, 65536);
        assertEquals("file.get_hash", call.getPayload().get("fun"));

        Map<String, Result<String>> response = call.callSync(client,
                new MinionList("minion1"));
        assertEquals("5a6e8ba50b0fae347f70ba75cf7738fdd1cef12009cc516171e72fa559e6daff",
                response.get("minion1").result().get());
    }

    @Test
    public final void testDirectoryExists() throws SaltException {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_TRUE_RESPONSE)));

        LocalCall<Boolean> call = File.directoryExists("/test/");
        assertEquals("file.directory_exists", call.getPayload().get("fun"));

        Map<String, Result<Boolean>> response = call.callSync(client,
                new MinionList("minion1"));
        assertTrue(response.get("minion1").result().get());
    }

    @Test
    public final void testFileExists() throws SaltException {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_TRUE_RESPONSE)));

        LocalCall<Boolean> call = File.fileExists("/test");
        assertEquals("file.file_exists", call.getPayload().get("fun"));

        Map<String, Result<Boolean>> response = call.callSync(client,
                new MinionList("minion1"));
        assertTrue(response.get("minion1").result().get());
    }

    @Test
    public final void testGetMode() throws SaltException {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_MODE_RESPONSE)));

        LocalCall<String> call = File.getMode("/test", true);
        assertEquals("file.get_mode", call.getPayload().get("fun"));

        Map<String, Result<String>> response = call.callSync(client,
                new MinionList("minion1"));
        assertEquals("0755", response.get("minion1").result().get());
    }

    @Test
    public final void testMkdir() throws SaltException {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_NULL_RESPONSE)));

        LocalCall<String> call = File.mkdir("/test");
        assertEquals("file.mkdir", call.getPayload().get("fun"));

        Map<String, Result<String>> response = call.callSync(client,
                new MinionList("minion1"));
        assertEquals(new GenericSaltError(JsonNull.INSTANCE),
                response.get("minion1").error().get());
    }

    @Test
    public final void testReaddir() throws SaltException {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_READDIR_RESPONSE)));

        LocalCall<List<String>> call = File.readdir("/test/");
        assertEquals("file.readdir", call.getPayload().get("fun"));

        Map<String, Result<List<String>>> response = call.callSync(client,
                new MinionList("minion1"));
        List<String> result = response.get("minion1").result().get();
        assertNotNull(result);
        assertArrayEquals(new String[]{".", "..", "test1", "test2", "test3"},
                result.toArray());

        stubFor(any(urlMatching("/")).willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_READDIR_EXCEPTION_RESPONSE)));

        response = call.callSync(client, new MinionList("minion1"));
        String errorMessage = "ERROR executing 'file.readdir': " +
                "A valid directory was not specified.";
        assertEquals(new GenericSaltError(new JsonPrimitive(errorMessage)),
                response.get("minion1").error().get());
    }

    @Test
    public final void testRmdir() throws SaltException {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_TRUE_RESPONSE)));

        LocalCall<Boolean> call = File.rmdir("/test/");
        assertEquals("file.rmdir", call.getPayload().get("fun"));

        Map<String, Result<Boolean>> response = call.callSync(client,
                new MinionList("minion1"));
        assertTrue(response.get("minion1").result().get());

        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_RMDIR_EXCEPTION_RESPONSE)));

        response = call.callSync(client, new MinionList("minion1"));
        String errorMessage = "ERROR: A valid directory was not specified.";
        assertEquals(new GenericSaltError(new JsonPrimitive(errorMessage)),
                response.get("minion1").error().get());
    }
}
