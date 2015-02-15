package com.suse.saltstack.netapi.parser;

import com.suse.saltstack.netapi.results.SaltStackJob;
import com.suse.saltstack.netapi.results.SaltStackResult;
import com.suse.saltstack.netapi.utils.SaltStackClientUtils;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SaltStackJobParserTest {

    public static String MINIONS_RESPONSE="{\"_links\": {\"jobs\": [{\"href\": \"/jobs/20150211105524392307\"}]}, \"return\": [{\"jid\": \"20150211105524392307\", \"minions\": [\"myminion\"]}]}";

    @Test
    public void testSaltStackJobParser() throws Exception {
        InputStream is = SaltStackClientUtils.stringToStream(MINIONS_RESPONSE);
        SaltStackResult<List<SaltStackJob>> result = SaltStackParser.JOB.parse(is);
        assertNotNull("failed to parse", result);
        assertEquals("unable to parse jid", "20150211105524392307", result.getResult().get(0).getJid());
    }
}