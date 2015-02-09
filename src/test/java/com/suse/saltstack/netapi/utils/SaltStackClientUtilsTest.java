package com.suse.saltstack.netapi.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * SaltStackClientUtils class unit-tests.
 */
public class SaltStackClientUtilsTest {

    @Test
    public void testCloseQuietly() throws IOException {
        // Close valid stream
        MockedInputStream is = new MockedInputStream();
        SaltStackClientUtils.closeQuietly(is);
        assertTrue(is.isClosed());
    }

    @Test
    public void testStringToStream() {
        final String TEST_STRING = "SUSE";
        InputStream is = SaltStackClientUtils.stringToStream(TEST_STRING);
        try (Scanner scanner = new Scanner(is)) {
            String result = scanner.nextLine();
            assertEquals("Result doesn't match test string", result, TEST_STRING);
        }
    }

    @Test
    public void testStreamToString() {
        final String TEST_STRING = "SUSE";
        String result = SaltStackClientUtils.streamToString(
                new ByteArrayInputStream(TEST_STRING.getBytes()));
        assertEquals("Result doesn't match test string", result, TEST_STRING);
    }
}
