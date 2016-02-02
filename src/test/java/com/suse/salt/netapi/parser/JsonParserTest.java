package com.suse.salt.netapi.parser;

import com.suse.salt.netapi.calls.wheel.Key;
import com.suse.salt.netapi.datatypes.Arguments;
import com.suse.salt.netapi.datatypes.Job;
import com.suse.salt.netapi.datatypes.ScheduledJob;
import com.suse.salt.netapi.datatypes.Token;
import com.suse.salt.netapi.datatypes.cherrypy.Applications;
import com.suse.salt.netapi.datatypes.cherrypy.HttpServer;
import com.suse.salt.netapi.datatypes.cherrypy.Request;
import com.suse.salt.netapi.datatypes.cherrypy.ServerThread;
import com.suse.salt.netapi.datatypes.cherrypy.Stats;
import com.suse.salt.netapi.results.Result;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.util.Date;
import java.util.Arrays;
import java.util.Optional;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;

/**
 * Json parser unit tests.
 */
public class JsonParserTest {

    @Test
    public void testJobsParser() throws Exception {
        InputStream is = getClass().getResourceAsStream("/minions_response.json");
        Result<List<ScheduledJob>> result = JsonParser.SCHEDULED_JOB.parse(is);
        assertNotNull("failed to parse", result);
        String jid = result.getResult().get(0).getJid();
        assertEquals("unable to parse jid", "20150211105524392307", jid);
    }

    @Test
    public void testStringParser() throws Exception {
        InputStream is = getClass().getResourceAsStream("/logout_response.json");
        Result<String> result = JsonParser.STRING.parse(is);
        assertNotNull(result);
    }

    @Test
    public void testTokenParser() throws Exception {
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
    public void testTokenParserWrongDate() throws Exception {
        InputStream is = getClass().getResourceAsStream("/login_response_wrong_date.json");
        JsonParser.TOKEN.parse(is);
    }

    @Test
    public void testTokenParserDateMissing() throws Exception {
        InputStream is = getClass()
                .getResourceAsStream("/login_response_missing_date.json");
        Token token = JsonParser.TOKEN.parse(is).getResult().get(0);
        assertNull(token.getStart());
        assertNull(token.getExpire());
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
            ServerThread thread = server.getWorkerThreads().get("CP Server Thread-" + i);
            assertEquals(0, thread.getBytesRead());
            assertEquals(2, thread.getBytesWritten());
            assertEquals(3.4, thread.getReadThroughput(), 0);
            assertEquals(5, thread.getRequests());
            assertEquals(6, thread.getWorkTime(), 0);
            assertEquals(7.8, thread.getWriteThroughput(), 0);
        }
    }

    @Test
    public void testKeysParser() throws Exception {
        InputStream is = getClass().getResourceAsStream("/keys_response.json");
        Result<Key.Names> result = JsonParser.KEYS.parse(is);
        Key.Names keys = result.getResult();
        assertNotNull("failed to parse", result);
        assertEquals(Arrays.asList("master.pem", "master.pub"), keys.getLocal());
        assertEquals(Arrays.asList("m1"), keys.getMinions());
        assertEquals(Arrays.asList("m2"), keys.getUnacceptedMinions());
        assertEquals(Arrays.asList("m3"), keys.getRejectedMinions());
    }

    @Test
    public void testJobsWithArgsParser() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/jobs_response.json");
        Result<List<Map<String, Job>>> result = JsonParser.JOBS.parse(is);
        assertNotNull("failed to parse", result);

        Map<String, Job> jobs = result.getResult().get(0);
        Job job = jobs.get("20150304200110485012");
        assertNotNull(job);
        Arguments expectedArgs = new Arguments();
        expectedArgs.getArgs().add("enable-autodestruction");
        assertEquals(expectedArgs.getArgs(), job.getArguments().getArgs());
        assertEquals(expectedArgs.getKwargs(), job.getArguments().getKwargs());
        assertEquals("test.echo", job.getFunction());
        assertEquals("*", job.getTarget());
        assertEquals("glob", job.getTargetType());
        assertEquals("chuck", job.getUser());
    }

    @Test
    public void testJobsWithKwargsParser() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/jobs_response_kwargs.json");
        Result<List<Map<String, Job>>> result = JsonParser.JOBS.parse(is);
        assertNotNull("failed to parse", result);

        Map<String, Job> jobs = result.getResult().get(0);
        Job job = jobs.get("20150306023815935637");
        assertNotNull(job);

        Arguments expectedArgs = new Arguments();
        expectedArgs.getArgs().add("i3");
        expectedArgs.getArgs().add(true);
        expectedArgs.getKwargs().put("sysupgrade", true);
        expectedArgs.getKwargs().put("otherkwarg", 42.5);

        assertEquals(expectedArgs.getArgs(), job.getArguments().getArgs());
        assertEquals(expectedArgs.getKwargs(), job.getArguments().getKwargs());
        assertEquals("pkg.install", job.getFunction());
        assertEquals("*", job.getTarget());
        assertEquals("glob", job.getTargetType());
        assertEquals("lucid", job.getUser());
    }

    @Test
    public void testOptionalParser() {
        InputStream is = this.getClass()
                .getResourceAsStream("/optional_parser_test.json");
        JsonParser<OptionalTest> parser = new JsonParser<>(new TypeToken<OptionalTest>(){});
        OptionalTest result = parser.parse(is);
        assertFalse(result.nullString.isPresent());
        assertFalse(result.absentString.isPresent());
        result.valueString.ifPresent((value) ->
                assertEquals("string with value", value)
        );
        List<Optional<Integer>> expected = new LinkedList<>();
        expected.add(Optional.of(1));
        expected.add(Optional.of(2));
        expected.add(Optional.of(3));
        expected.add(Optional.empty());
        expected.add(Optional.of(5));
        assertEquals(expected, result.maybeInts);
    }

    @Test
    public void testOptionalSingleValue() {
        JsonParser<Optional<Integer>> parser =
                new JsonParser<>(new TypeToken<Optional<Integer>>(){});
        InputStream nullValue = this.getClass()
                .getResourceAsStream("/single_null_value.json");
        assertFalse(parser.parse(nullValue).isPresent());

        InputStream intValue = this.getClass()
                .getResourceAsStream("/single_int_value.json");
        assertEquals(new Integer(123), parser.parse(intValue).get());
    }

    @Test
    public void testJobsWithArgsAsKwargsParser() throws Exception {
        InputStream is = this.getClass()
                .getResourceAsStream("/jobs_response_args_as_kwargs.json");
        Result<List<Map<String, Job>>> result = JsonParser.JOBS.parse(is);

        Map<String, Job> jobs = result.getResult().get(0);
        Job job = jobs.get("20150315163041425361");

        Arguments expectedArgs = new Arguments();
        Map<String, Object> arg = new LinkedHashMap<String, Object>() {
            {
                put("refresh", true);
            }
        };
        expectedArgs.getArgs().add(arg);

        arg = new LinkedHashMap<String, Object>() {
            {
                put("somepar", 123.3);
                put("__kwarg__", false);
            }
        };
        expectedArgs.getArgs().add(arg);

        arg = new LinkedHashMap<String, Object>() {
            {
                put("nullparam", null);
                put("__kwarg__", null);
            }
        };
        expectedArgs.getArgs().add(arg);

        arg = new LinkedHashMap<String, Object>() {
            {
                put("otherparam", true);
                put("__kwarg__", 123.0);
            }
        };
        expectedArgs.getArgs().add(arg);
        expectedArgs.getArgs().add("i3");
        expectedArgs.getKwargs().put("sysupgrade", true);

        assertEquals(expectedArgs.getArgs(), job.getArguments().getArgs());
        assertEquals(expectedArgs.getKwargs(), job.getArguments().getKwargs());
    }

    @Test
    public void testJobsMultipleKwargs() throws Exception {
        InputStream is = this.getClass()
                .getResourceAsStream("/jobs_response_multiple_kwarg.json");
        Result<List<Map<String, Job>>> result = JsonParser.JOBS.parse(is);

        Map<String, Job> jobs = result.getResult().get(0);
        Job job = jobs.get("20150306023815935637");

        Arguments expectedArgs = new Arguments();
        expectedArgs.getKwargs().put("multi", false);

        assertEquals(expectedArgs.getKwargs(), job.getArguments().getKwargs());
        assertEquals(0, job.getArguments().getArgs().size());
        assertEquals(1, job.getArguments().getKwargs().size());
    }
}
