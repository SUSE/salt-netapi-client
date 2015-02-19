package com.suse.saltstack.netapi.parser;

import com.suse.saltstack.netapi.results.Job;
import com.suse.saltstack.netapi.results.Result;
import com.suse.saltstack.netapi.results.Token;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SaltStackParserTest {

    @Test
    public void testSaltStackJobParser() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/minions_response.json");
        Result<List<Job>> result = JsonParser.JOB.parse(is);
        assertNotNull("failed to parse", result);
        assertEquals("unable to parse jid", "20150211105524392307", result.getResult().get(0).getJid());
    }

    @Test
    public void testSaltStackStringParser() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/logout_response.json");
        Result<String> result = JsonParser.STRING.parse(is);
        assertNotNull(result);
    }

    @Test
    public void testSaltStackTokenParser() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/login_response.json");
        Result<List<Token>> result = JsonParser.TOKEN.parse(is);
        assertNotNull(result);
        assertEquals("", "salt", result.getResult().get(0).getUser());
        assertEquals("", "pam", result.getResult().get(0).getEauth());
        assertEquals("", "f248284b655724ca8a86bcab4b8df608ebf5b08b", result.getResult().get(0).getToken());
    }
}
