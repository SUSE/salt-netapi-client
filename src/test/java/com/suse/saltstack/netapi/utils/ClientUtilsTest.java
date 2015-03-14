package com.suse.saltstack.netapi.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * ClientUtils class unit-tests.
 */
public class ClientUtilsTest {

    @Test
    public void testCloseQuietly() throws IOException {
        /**
         * Mocked InputStream that throws IOException when closed more than once.
         */
        class MockedInputStream extends InputStream {
            private boolean closed = false;

            boolean isClosed() {
                return closed;
            }

            @Override
            public int read() throws IOException {
                return 0;
            }

            @Override
            public void close() throws IOException {
                if (closed) {
                    throw new IOException("Stream already closed");
                }
                closed = true;
            }
        }

        // Close valid stream
        MockedInputStream is = new MockedInputStream();
        ClientUtils.closeQuietly(is);
        assertTrue(is.isClosed());
    }

    @Test
    public void testStringToStream() {
        final String TEST_STRING = "SUSE";
        InputStream is = ClientUtils.stringToStream(TEST_STRING);
        try (Scanner scanner = new Scanner(is)) {
            String result = scanner.nextLine();
            assertEquals("Result doesn't match test string", result, TEST_STRING);
        }
    }

    @Test
    public void testStreamToString() {
        final String TEST_STRING = "SUSE";
        String result = ClientUtils.streamToString(
                new ByteArrayInputStream(TEST_STRING.getBytes()));
        assertEquals("Result doesn't match test string", result, TEST_STRING);
    }

    @Test
    public void makeJsonDataEmpty() {
        JsonObject jsonObject = ClientUtils.makeJsonData(null, null, null);
        assertEquals(new JsonObject(), jsonObject);
    }

    @Test
    public void makeJsonDataKwargsArgs() {
        JsonObject expected = new JsonObject();
        expected.addProperty("tgt", "*");
        expected.addProperty("fun", "test.ping");
        JsonObject kwarg = new JsonObject();
        kwarg.addProperty("first", "1");
        kwarg.addProperty("snd", "42");
        expected.add("kwarg", kwarg);
        JsonArray arg = new JsonArray();
        arg.add(new JsonPrimitive("foo"));
        arg.add(new JsonPrimitive("bar"));
        expected.add("arg", arg);

        Map<String, String> props = new LinkedHashMap<String, String>() {
            {
                put("tgt", "*");
                put("fun", "test.ping");
            }
        };

        Map<String, String> kwargs = new LinkedHashMap<String, String>() {
            {
                put("first", "1");
                put("snd", "42");
            }
        };

        List<String> args = new ArrayList<>();
        args.add("foo");
        args.add("bar");

        JsonObject jsonObject = ClientUtils.makeJsonData(props, kwargs, args);

        assertEquals(expected, jsonObject);
    }
}
