package com.suse.saltstack.netapi.parser;

import com.suse.saltstack.netapi.results.SaltStackStringResult;
import com.suse.saltstack.netapi.utils.SaltStackClientUtils;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

public class SaltStackStringParserTest {

    public static String LOGOUT_RESPONSE="{\"clients\": [\"local\", \"local_async\", \"local_batch\", \"runner\", \"runner_async\", \"wheel\", \"wheel_async\"], \"return\": \"Welcome\"}";

    @Test
    public void testSaltStackStringParser() throws Exception {
        InputStream is = SaltStackClientUtils.stringToStream(LOGOUT_RESPONSE);
        SaltStackStringParser parser = new SaltStackStringParser();
        SaltStackStringResult result = parser.parse(is);
        assertNotNull(result);
    }

}