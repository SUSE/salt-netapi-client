package com.suse.salt.netapi.calls.modules;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.Assert.assertEquals;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;
import java.util.Map;

import com.suse.salt.netapi.results.SaltError;
import com.suse.salt.netapi.utils.Xor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.datatypes.target.MinionList;
import com.suse.salt.netapi.exception.SaltException;
import com.suse.salt.netapi.utils.ClientUtils;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

/**
 * SaltUtil unit tests.
 */
public class SmbiosTest {

    private static final int MOCK_HTTP_PORT = 8888;

    static final String JSON_RECORDS_RESPONSE = ClientUtils.streamToString(
            SmbiosTest.class.getResourceAsStream("/modules/smbios/bios_records.json"));

    static final String JSON_EMPTY_RESPONSE = ClientUtils.streamToString(
            SmbiosTest.class.getResourceAsStream("/modules/smbios/empty_response.json"));

    static final String JSON_ERROR_RESPONSE = ClientUtils.streamToString(
            SmbiosTest.class.getResourceAsStream("/modules/smbios/error_response.json"));

    static final String JSON_ALL_RESPONSE = ClientUtils.streamToString(
            SmbiosTest.class.getResourceAsStream("/modules/smbios/all_records.json"));

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(MOCK_HTTP_PORT);

    private SaltClient client;

    @Before
    public void init() {
        URI uri = URI.create("http://localhost:" + Integer.toString(MOCK_HTTP_PORT));
        client = new SaltClient(uri);
    }

    @Test
    public void testRecordsOne() throws SaltException {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_RECORDS_RESPONSE)));

        Map<String, Xor<SaltError, List<Smbios.Record>>> response = Smbios.records(Smbios.RecordType.BIOS)
                .callSync(client, new MinionList("minion1"));

        assertEquals(1, response.size());
        Map.Entry<String, Xor<SaltError, List<Smbios.Record>>> first = response
                .entrySet().iterator().next();
        Smbios.Record record = first.getValue().right().get().get(0);
        assertEquals("minion1", first.getKey());
        assertEquals("BIOS Information", record.getDescription());
        assertEquals("0xE8000", record.getData().get("address"));
        assertEquals(2, ((List<?>) record.getData()
                .get("characteristics")).size()) ;
        assertEquals("04/01/2014", record.getData().get("release_date"));
        assertEquals("64 kB", record.getData().get("rom_size"));
        assertEquals("96 kB", record.getData().get("runtime_size"));
        assertEquals("SeaBIOS", record.getData().get("vendor"));
        assertEquals("rel-1.7.5-0-ge51488c-20150524_160643-cloud127", record.getData().get("version"));
    }

    @Test
    public void testRecordsAll() throws SaltException {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_ALL_RESPONSE)));

        Map<String, Xor<SaltError, List<Smbios.Record>>> response = Smbios.records(null)
                .callSync(client, new MinionList("minion1"));

        assertEquals(1, response.size());
        Map.Entry<String, Xor<SaltError, List<Smbios.Record>>> first = response
                .entrySet().iterator().next();
        assertEquals("minion1", first.getKey());
        assertEquals(7, first.getValue().right().get().size());
    }

    @Test
    public void testEmptyResponse() throws SaltException {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_EMPTY_RESPONSE)));

        Map<String, Xor<SaltError, List<Smbios.Record>>> response = Smbios.records(Smbios.RecordType.BIOS)
                .callSync(client, new MinionList("minion1"));
        assertEquals(0, response.size());
    }

    @Test(expected = com.google.gson.JsonSyntaxException.class)
    public void testErrorResponse() throws SaltException {
        stubFor(any(urlMatching("/"))
                .willReturn(aResponse()
                .withStatus(HttpURLConnection.HTTP_OK)
                .withHeader("Content-Type", "application/json")
                .withBody(JSON_ERROR_RESPONSE)));

        Smbios.records(Smbios.RecordType.BIOS)
                .callSync(client, new MinionList("minion1"));
    }
}
