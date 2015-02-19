package com.suse.saltstack.netapi.parser;

import com.suse.saltstack.netapi.results.SaltStackJob;
import com.suse.saltstack.netapi.results.SaltStackResult;
import com.suse.saltstack.netapi.results.SaltStackToken;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SaltStackParserTest {

    @Test
    public void testSaltStackJobParser() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/minions_response.json");
        SaltStackResult<List<SaltStackJob>> result = SaltStackParser.JOB.parse(is);
        assertNotNull("failed to parse", result);
        assertEquals("unable to parse jid", "20150211105524392307", result.getResult().get(0).getJid());
    }

    @Test
    public void testSaltStackStringParser() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/logout_response.json");
        SaltStackResult<String> result = SaltStackParser.STRING.parse(is);
        assertNotNull(result);
    }

    @Test
    public void testSaltStackTokenParser() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/login_response.json");
        SaltStackResult<List<SaltStackToken>> result = SaltStackParser.TOKEN.parse(is);
        assertNotNull(result);
        assertEquals("", "salt", result.getResult().get(0).getUser());
        assertEquals("", "pam", result.getResult().get(0).getEauth());
        assertEquals("", "f248284b655724ca8a86bcab4b8df608ebf5b08b", result.getResult().get(0).getToken());
    }
}
