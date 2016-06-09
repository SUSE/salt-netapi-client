package com.suse.salt.netapi.calls;

import com.suse.salt.netapi.calls.modules.Cmd;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Tests for LocalCall
 */
public class LocalCallTest {

    @Test
    public void testWithMetadata() {
        LocalCall<String> run = Cmd.run("echo 'hello world'");
        assertFalse(run.getPayload().containsKey("metadata"));

        LocalCall<String> runWithMetadata = run.withMetadata("myMetadata");
        assertFalse(run.getPayload().containsKey("metadata"));
        assertTrue(runWithMetadata.getPayload().containsKey("metadata"));
        assertEquals(runWithMetadata.getPayload().get("metadata"), "myMetadata");

        LocalCall<String> runWithoutMetadata = run.withoutMetadata();
        assertFalse(runWithoutMetadata.getPayload().containsKey("metadata"));
        assertTrue(runWithMetadata.getPayload().containsKey("metadata"));
        assertEquals(runWithMetadata.getPayload().get("metadata"), "myMetadata");
    }

}
