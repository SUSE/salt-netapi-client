package com.suse.salt.netapi.calls.modules;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.junit.Test;

import com.suse.salt.netapi.calls.modules.Cmd;
import com.suse.salt.netapi.parser.JsonParser;

import com.google.gson.reflect.TypeToken;

/**
 * Cmd unit tests.
 */
public class CmdTest {

    @Test
    public void testCmdUptime() {
        TypeToken<String> type = Cmd.run("uptime").getReturnType();
        InputStream is = this.getClass()
                .getResourceAsStream("/modules/cmd/uptime.json");

        JsonParser<String> parser = new JsonParser<>(type);
        String parsed = parser.parse(is);
        assertEquals(" 16:51:23 up 22 min,  0 users,  load average: 0.00, 0.02, 0.09",
                parsed);
    }
}
