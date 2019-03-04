package com.suse.salt.netapi.calls.modules;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.suse.salt.netapi.calls.LocalCall;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.client.impl.HttpAsyncClientImpl;
import com.suse.salt.netapi.datatypes.AuthMethod;
import com.suse.salt.netapi.datatypes.Token;
import com.suse.salt.netapi.datatypes.target.MinionList;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.utils.ClientUtils;
import com.suse.salt.netapi.utils.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Grains unit tests.
 */
public class GrainsTest {

    private static final int MOCK_HTTP_PORT = 8888;

    static final String JSON_ITEM_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/grains/item.json"));

    static final String JSON_ITEMS_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/grains/items.json"));

    static final String JSON_HASVALUE_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/grains/hasvalue.json"));

    static final String JSON_SET_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/grains/set.json"));

    static final String JSON_SETVALUE_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/grains/setvalue.json"));

    static final String JSON_SETVALUES_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/grains/setvalues.json"));

    static final String JSON_APPEND_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/grains/append.json"));

    static final String JSON_REMOVE_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/grains/remove.json"));

    static final String JSON_NULL_RESPONSE = ClientUtils.streamToString(
            SaltUtilTest.class.getResourceAsStream("/modules/grains/null.json"));

    private SaltClient client;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(MOCK_HTTP_PORT);

    static final AuthMethod AUTH = new AuthMethod(new Token());

    @Before
    public void init() {
        URI uri = URI.create("http://localhost:" + Integer.toString(MOCK_HTTP_PORT));
        client = new SaltClient(uri, new HttpAsyncClientImpl(TestUtils.defaultClient()));
    }

    private static void mockOkResponseWith(String json) {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                        .withStatus(HttpURLConnection.HTTP_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBody(json)));
    }

    @Test
    public final void testItem() {
        LocalCall<Map<String, Object>> call = Grains.item(false, "os");
        assertEquals("grains.item", call.getPayload().get("fun"));

        mockOkResponseWith(JSON_ITEM_RESPONSE);

        Map<String, Result<Map<String, Object>>> response =
                call.callSync(client, new MinionList("minion"), AUTH)
                        .toCompletableFuture().join();

        assertNotNull(response.get("minion"));


        Map<String, Object> output = response.get("minion").result().get();

        Map<String, Object> desiredResult = new HashMap<>();
        desiredResult.put("os", "SUSE");

        assertEquals(desiredResult, output);
    }

    @Test
    public final void testItems() {
        LocalCall<Map<String, Object>> call = Grains.items(false);
        assertEquals("grains.items", call.getPayload().get("fun"));

        mockOkResponseWith(JSON_ITEMS_RESPONSE);

        Map<String, Result<Map<String, Object>>> response =
                call.callSync(client, new MinionList("minion"), AUTH)
                        .toCompletableFuture().join();

        assertNotNull(response.get("minion"));

        Map<String, Object> output = response.get("minion").result().get();

        Map<String, Object> desiredResult = new HashMap<>();
        desiredResult.put("kernelrelease", "4.4.73-5-default");
        desiredResult.put("virtual", "kvm");

        assertEquals(desiredResult, output);
    }

    @Test
    public final void testHasValue() {
        LocalCall<Boolean> call = Grains.hasValue("os_family");
        assertEquals("grains.has_value", call.getPayload().get("fun"));

        mockOkResponseWith(JSON_HASVALUE_RESPONSE);

        Map<String, Result<Boolean>> response =
                call.callSync(client, new MinionList("minion"), AUTH)
                        .toCompletableFuture().join();

        assertNotNull(response.get("minion"));

        Boolean output = response.get("minion").result().get();

        assertTrue(output);
    }

    @Test
    public final void testSet() {
        Map<String, Object> args = new HashMap<>();
        args.put("val", "1234");
        args.put("force", true);

        LocalCall<Map<String, Object>> call = Grains.set("serialnumber", Optional.of(args));
        assertEquals("grains.set", call.getPayload().get("fun"));

        mockOkResponseWith(JSON_SET_RESPONSE);

        Map<String, Result<Map<String, Object>>> response =
                call.callSync(client, new MinionList("minion"), AUTH)
                        .toCompletableFuture().join();

        assertNotNull(response.get("minion"));

        @SuppressWarnings("unchecked")
        Map<String, Object> output = (Map<String, Object>) response.get("minion").result().get().get("changes");

        Map<String, Object> desiredResult = new HashMap<>();
        desiredResult.put("serialnumber", "1234");

        assertEquals(desiredResult, output);
    }

    @Test
    public final void testSetValue() {
        LocalCall<Map<String, Object>> call = Grains.setValue("productname", "ABCD", Optional.of(false));
        assertEquals("grains.setval", call.getPayload().get("fun"));

        mockOkResponseWith(JSON_SETVALUE_RESPONSE);

        Map<String, Result<Map<String, Object>>> response =
                call.callSync(client, new MinionList("minion"), AUTH)
                        .toCompletableFuture().join();

        assertNotNull(response.get("minion"));

        Map<String, Object> output = response.get("minion").result().get();

        Map<String, Object> desiredResult = new HashMap<>();
        desiredResult.put("productname", "ABCD");

        assertEquals(desiredResult, output);
    }

    @Test
    public final void testSetValues() {
        Map<String, Object> grains = new HashMap<>();
        grains.put("hostname", "minior");
        grains.put("server_id", "3A9D");

        LocalCall<Map<String, Object>> call = Grains.setValues(grains, Optional.of(true));
        assertEquals("grains.setvals", call.getPayload().get("fun"));

        mockOkResponseWith(JSON_SETVALUES_RESPONSE);

        Map<String, Result<Map<String, Object>>> response =
                call.callSync(client, new MinionList("minion"), AUTH)
                        .toCompletableFuture().join();

        assertNotNull(response.get("minion"));

        Map<String, Object> output = response.get("minion").result().get();

        Map<String, Object> desiredResult = new HashMap<>();
        desiredResult.put("server_id", "3A9D");
        desiredResult.put("hostname", "minion");

        assertEquals(desiredResult, output);
    }

    @Test
    public final void testAppend() {
        LocalCall<Map<String, Object>> call = Grains.append("saltversioninfo", "rc1",
                Optional.of(true), Optional.empty());
        assertEquals("grains.append", call.getPayload().get("fun"));

        mockOkResponseWith(JSON_APPEND_RESPONSE);

        Map<String, Result<Map<String, Object>>> response =
                call.callSync(client, new MinionList("minion"), AUTH)
                        .toCompletableFuture().join();

        assertNotNull(response.get("minion"));

        String output = response.get("minion").result().get().toString();

        assertEquals("{saltversioninfo=[2019.0, 2.0, 0.0, 0.0, rc1]}", output);
    }

    @Test
    public final void testDeleteKey() {
        LocalCall<Map<String, Object>> call = Grains.deleteKey("machine_id");
        assertEquals("grains.delkey", call.getPayload().get("fun"));

        mockOkResponseWith(JSON_NULL_RESPONSE);

        Map<String, Result<Map<String, Object>>> response =
                call.callSync(client, new MinionList("minion"), AUTH)
                        .toCompletableFuture().join();

        assertNotNull(response.get("minion"));

        Map<String, Object> output = response.get("minion").result().get();

        //Does not return any information
        assertTrue(output.isEmpty());
    }

    @Test
    public final void testDeleteValue() {
        LocalCall<Map<String, Object>> call = Grains.deleteValue("machine_id", Optional.empty());
        assertEquals("grains.delval", call.getPayload().get("fun"));

        mockOkResponseWith(JSON_NULL_RESPONSE);

        Map<String, Result<Map<String, Object>>> response =
                call.callSync(client, new MinionList("minion"), AUTH)
                        .toCompletableFuture().join();

        assertNotNull(response.get("minion"));

        Map<String, Object> output = response.get("minion").result().get();

        //Does not return any information
        assertTrue(output.isEmpty());
    }

    @Test
    public final void testRemove() {
        LocalCall<Map<String, Object>> call = Grains.remove("saltversioninfo", "rc1", Optional.empty());
        assertEquals("grains.remove", call.getPayload().get("fun"));

        mockOkResponseWith(JSON_REMOVE_RESPONSE);

        Map<String, Result<Map<String, Object>>> response =
                call.callSync(client, new MinionList("minion"), AUTH)
                        .toCompletableFuture().join();

        assertNotNull(response.get("minion"));

        String output = response.get("minion").result().get().toString();

        assertEquals("{saltversioninfo=[2019.0, 2.0, 0.0, 0.0]}", output);
    }
}
