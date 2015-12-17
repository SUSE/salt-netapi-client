package com.suse.saltstack.netapi.calls.modules;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.Assert.assertEquals;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.suse.saltstack.netapi.client.SaltStackClient;
import com.suse.saltstack.netapi.datatypes.target.MinionList;
import com.suse.saltstack.netapi.exception.SaltStackException;
import com.suse.saltstack.netapi.utils.ClientUtils;

/**
 * SaltUtil unit tests.
 */
public class SmbiosTest {

    private static final int MOCK_HTTP_PORT = 8888;

    static final String JSON_RECORDS_RESPONSE = ClientUtils.streamToString(
            SmbiosTest.class.getResourceAsStream("/modules/smbios/bios_records.json"));

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(MOCK_HTTP_PORT);

    private SaltStackClient client;

    @Before
    public void init() {
        URI uri = URI.create("http://localhost:" + Integer.toString(MOCK_HTTP_PORT));
        client = new SaltStackClient(uri);
    }

    @Test
    public void testRecords() throws SaltStackException {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_RECORDS_RESPONSE)));

        Map<String, List<Smbios.Record>> response = Smbios.records(Smbios.RecordType.BIOS)
                .callSync(client, new MinionList("minion1"));

        assertEquals(response.size(), 1);
        Map.Entry<String, List<Smbios.Record>> first = response
                .entrySet().iterator().next();
        assertEquals(first.getKey(), "minion1");
        assertEquals(first.getValue().get(0).getDescription(), "BIOS Information");
        assertEquals(first.getValue().get(0).getData().get("address"), "0xE8000");
        assertEquals(((List) first.getValue().get(0).getData()
                .get("characteristics")).size(), 2);
        assertEquals(first.getValue().get(0).getData().get("release_date"), "04/01/2014");
        assertEquals(first.getValue().get(0).getData().get("rom_size"), "64 kB");
        assertEquals(first.getValue().get(0).getData().get("runtime_size"), "96 kB");
        assertEquals(first.getValue().get(0).getData().get("vendor"), "SeaBIOS");
        assertEquals(first.getValue().get(0).getData().get("version"),
                "rel-1.7.5-0-ge51488c-20150524_160643-cloud127");
    }

}
