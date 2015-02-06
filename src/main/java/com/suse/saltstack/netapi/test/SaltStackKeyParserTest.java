package com.suse.saltstack.netapi.test;

import com.suse.saltstack.netapi.parser.SaltStackKeyParser;
import com.suse.saltstack.netapi.client.SaltStackKeyResult;
import com.suse.saltstack.netapi.utils.SaltStackClientUtils;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;

import static org.junit.Assert.*;

public class SaltStackKeyParserTest {

    public static String KEYS_JSON = "{\"return\": {\"local\": {\"master.pem\": \"f9:80:02:8b:31:98:e8:14:50:ab:b9:e0:50:ff:53:69\", \"master.pub\": \"2a:d6:c4:b4:3e:c7:ee:b1:65:ce:f9:75:a7:72:68:bb\"}, \"minions\": {\"myminion\": \"9b:74:93:89:e3:08:c6:d0:5e:54:1d:f8:fd:91:b8:0a\"}}}";

    @Test
    public void testParse() throws Exception {
        SaltStackKeyParser parser = new SaltStackKeyParser();
        InputStream is = SaltStackClientUtils.stringToStream(KEYS_JSON);
        SaltStackKeyResult result = parser.parse(SaltStackKeyResult.class, is);
        assertNotNull(result);
        assertEquals("local key master.pem not found", result.getLocal().get(0).getMinionId(), "master.pem");
        assertEquals("minion key myminion not found", result.getMinions().get(0).getMinionId(), "myminion");
    }
}