package com.suse.saltstack.netapi.parser;

import com.suse.saltstack.netapi.results.SaltStackJob;
import com.suse.saltstack.netapi.results.SaltStackResult;
import com.suse.saltstack.netapi.results.SaltStackToken;
import com.suse.saltstack.netapi.utils.SaltStackClientUtils;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SaltStackParserTest {


    @Test
    public void testSaltStackJobParser() throws Exception {
        String MINIONS_RESPONSE = "{\"_links\": {\"jobs\": [{\"href\":" +
                " \"/jobs/20150211105524392307\"}]}, \"return\": [{\"jid\":" +
                " \"20150211105524392307\", \"minions\": [\"myminion\"]}]}";
        InputStream is = SaltStackClientUtils.stringToStream(MINIONS_RESPONSE);
        SaltStackResult<List<SaltStackJob>> result = SaltStackParser.JOB.parse(is);
        assertNotNull("failed to parse", result);
        assertEquals("unable to parse jid", "20150211105524392307", result.getResult().get(0).getJid());
    }


    @Test
    public void testSaltStackStringParser() throws Exception {
        String LOGOUT_RESPONSE = "{\"clients\": [\"local\", \"local_async\", " +
                "\"local_batch\", \"runner\", \"runner_async\", \"wheel\", " +
                "\"wheel_async\"], \"return\": \"Welcome\"}";
        InputStream is = SaltStackClientUtils.stringToStream(LOGOUT_RESPONSE);
        SaltStackResult<String> result = SaltStackParser.STRING.parse(is);
        assertNotNull(result);
    }


    @Test
    public void testSaltStackTokenParser() throws Exception {
        String LOGIN_JSON = "{\"return\": [{\"perms\": [\".*\", \"@wheel\", \"@runner\", " +
                "\"@jobs\"], \"start\": 1423573511.380074, \"token\": " +
                "\"f248284b655724ca8a86bcab4b8df608ebf5b08b\", \"expire\": " +
                "1423616711.38008, \"user\": \"salt\", \"eauth\": \"pam\"}]}";
        InputStream is = SaltStackClientUtils.stringToStream(LOGIN_JSON);
        SaltStackResult<List<SaltStackToken>> result = SaltStackParser.TOKEN.parse(is);
        assertNotNull(result);
        assertEquals("", "salt", result.getResult().get(0).getUser());
        assertEquals("", "pam", result.getResult().get(0).getEauth());
        assertEquals("", "f248284b655724ca8a86bcab4b8df608ebf5b08b", result.getResult().get(0).getToken());
    }
}