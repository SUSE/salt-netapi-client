package com.suse.saltstack.netapi.parser;

import com.google.gson.JsonParseException;
import com.suse.saltstack.netapi.datatypes.Job;
import com.suse.saltstack.netapi.datatypes.cherrypy.*;
import com.suse.saltstack.netapi.results.Result;
import com.suse.saltstack.netapi.datatypes.Token;
import java.util.Date;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JsonParserTest {

    @Test
    public void testSaltStackJobParser() throws Exception {
        InputStream is = getClass().getResourceAsStream("/minions_response.json");
        Result<List<Job>> result = JsonParser.JOB.parse(is);
        assertNotNull("failed to parse", result);
        String jid = result.getResult().get(0).getJid();
        assertEquals("unable to parse jid", "20150211105524392307", jid);
    }

    @Test
    public void testSaltStackStringParser() throws Exception {
        InputStream is = getClass().getResourceAsStream("/logout_response.json");
        Result<String> result = JsonParser.STRING.parse(is);
        assertNotNull(result);
    }

    @Test
    public void testSaltStackTokenParser() throws Exception {
        InputStream is = getClass().getResourceAsStream("/login_response.json");
        Result<List<Token>> result = JsonParser.TOKEN.parse(is);
        assertNotNull(result);
        assertEquals("user", result.getResult().get(0).getUser());
        assertEquals("auto", result.getResult().get(0).getEauth());
        String token = result.getResult().get(0).getToken();
        assertEquals("f248284b655724ca8a86bcab4b8df608ebf5b08b", token);
        assertEquals(new Date(1423573511380L), result.getResult().get(0).getStart());
        assertEquals(new Date(1423616711380L), result.getResult().get(0).getExpire());
    }

    @Test(expected =  JsonParseException.class)
    public void testSaltStackTokenParserWrongDate() throws Exception {
        InputStream is = getClass().getResourceAsStream("/login_response_wrong_date.json");
        JsonParser.TOKEN.parse(is);
    }

    @Test
    public void testStatsParser() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/stats_response.json");
        Stats result = JsonParser.STATS.parse(is);
        assertNotNull(result);

        Applications applications = result.getApplications();
        assertNotNull(applications);
        assertEquals(47.72178888320923, applications.getUptime(), 0);
        assertEquals(1.1315585135535497, applications.getReadsPerSecond(), 0);
        assertEquals(new Date(1425821785119L), applications.getCurrentTime());
        assertEquals("3.6.0", applications.getServerVersion());
        assertEquals(0.06533312797546387, applications.getTotalTime(), 0);
        assertEquals(true, applications.isEnabled());
        assertEquals(new Date(1425821737397L), applications.getStartTime());
        assertEquals(3.918541522794187, applications.getWritesPerSecond(), 0);
        assertEquals(54, applications.getTotalBytesRead());
        assertEquals(1, applications.getCurrentRequests());
        assertEquals(2, applications.getTotalRequests());
        assertEquals(27.0, applications.getReadsPerRequest(), 0);
        assertEquals(187, applications.getTotalBytesWritten());
        assertEquals(0.04190954295959868, applications.getRequestsPerSecond(), 0);
        assertEquals(93.5, applications.getWritesPerRequest(), 0);

        Request req1 = applications.getRequests().get("140691837540096");
        assertEquals(new Integer(54), req1.getBytesRead());
        assertEquals(new Integer(187), req1.getBytesWritten());
        assertEquals("200 OK", req1.getResponeStatus());
        assertEquals(new Date(1425821772580L), req1.getStartTime());
        assertEquals(new Date(1425821772645L), req1.getEndTime());
        assertEquals("127.0.0.1:45009", req1.getClient());
        assertEquals(0.06533312797546387, req1.getProcessingTime(), 0);
        assertEquals("POST /login HTTP/1.1", req1.getRequestLine());

        Request req2 = applications.getRequests().get("140691829147392");
        assertEquals(null, req2.getBytesRead());
        assertEquals(null, req2.getBytesWritten());
        assertEquals(null, req2.getResponeStatus());
        assertEquals(new Date(1425821785119L), req2.getStartTime());
        assertEquals(null, req2.getEndTime());
        assertEquals("127.0.0.1:45015", req2.getClient());
        assertEquals(0.0002930164337158203, req2.getProcessingTime(), 0);
        assertEquals("GET /stats HTTP/1.1", req2.getRequestLine());

        HttpServer server = result.getHttpServer();
        assertNotNull(server);
        assertEquals(-1, server.getBytesRead());
        assertEquals(0.0, server.getAcceptsPerSecond(), 0);
        assertEquals(2, server.getSocketErrors());
        assertEquals(3, server.getAccepts());
        assertEquals(99, server.getThreadsIdle());
        assertEquals(false, server.isEnable());
        assertEquals("('0.0.0.0', 8000)", server.getBindAddress());
        assertEquals(4, server.getReadThroughput());
        assertEquals(5, server.getQueue());
        assertEquals(6, server.getRunTime());
        assertEquals(3, server.getThreads());
        assertEquals(7, server.getBytesWritten());
        assertEquals(8, server.getRequests());
        assertEquals(9.0, server.getWorkTime(), 0);
        assertEquals(10, server.getWriteThroughput(), 0);

        for (int i = 0; i < server.getThreads(); i++) {
            ServerThread thread = server.getWorkerThreads().get("CP Server Thread-"+i);
            assertEquals(0, thread.getBytesRead());
            assertEquals(2, thread.getBytesWritten());
            assertEquals(3.4, thread.getReadThroughput(), 0);
            assertEquals(5, thread.getRequests());
            assertEquals(6, thread.getWorkTime(), 0);
            assertEquals(7.8, thread.getWriteThroughput(), 0);
        }
    }
}
