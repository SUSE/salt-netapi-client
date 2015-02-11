package com.suse.saltstack.netapi.parser;

import com.suse.saltstack.netapi.client.SaltStackTokenResult;
import com.suse.saltstack.netapi.utils.SaltStackClientUtils;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SaltStackTokenParserTest {

    public static String LOGIN_JSON = "{\"return\": [{\"perms\": [\".*\", \"@wheel\", \"@runner\", \"@jobs\"], \"start\": 1423573511.380074, \"token\": \"f248284b655724ca8a86bcab4b8df608ebf5b08b\", \"expire\": 1423616711.38008, \"user\": \"salt\", \"eauth\": \"pam\"}]}";

    @Test
    public void testSaltStackTokenParser() throws Exception {
        InputStream is = SaltStackClientUtils.stringToStream(LOGIN_JSON);
        SaltStackTokenParser parser = new SaltStackTokenParser();
        SaltStackTokenResult result = parser.parse(is);
        assertNotNull(result);
        assertEquals("", "salt", result.getResult().get(0).getUser());
        assertEquals("", "pam", result.getResult().get(0).getEauth());
        assertEquals("", "f248284b655724ca8a86bcab4b8df608ebf5b08b", result.getResult().get(0).getToken());
    }

}