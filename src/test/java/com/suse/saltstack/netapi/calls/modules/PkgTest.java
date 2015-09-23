package com.suse.saltstack.netapi.calls.modules;

import com.google.gson.reflect.TypeToken;
import com.suse.saltstack.netapi.parser.JsonParser;

import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Pkg unit tests.
 */
public class PkgTest {

    @Test
    public void testInfoInstalled() {
        TypeToken<Map<String, Pkg.Info>> type = Pkg.infoInstalled("vim").getReturnType();
        InputStream is = this.getClass()
                .getResourceAsStream("/modules/pkg/info_installed.json");
        JsonParser<Map<String, Pkg.Info>> parser = new JsonParser<>(type);
        Map<String, Pkg.Info> parsed = parser.parse(is);
        Pkg.Info info = parsed.get("vim");

        assertEquals(ZonedDateTime.of(2014, 10, 8, 3, 44, 36, 0, ZoneId.of("Z")),
                info.getBuildDate());
        assertEquals("Productivity/Editors/Vi", info.getGroup());
        assertEquals("vim", info.getName());
        assertEquals("Vim", info.getLicense());
        assertEquals("sheep21", info.getBuildHost());
        assertEquals("http://www.vim.org/", info.getUrl());
        assertEquals("(not relocatable)", info.getRelocations());
        assertEquals(ZonedDateTime.of(2015, 8, 24, 5, 58, 22, 0, ZoneId.of("Z")),
                info.getInstallDate());
        assertEquals("Vi IMproved", info.getSummary());
        assertEquals("vim-7.4.326-2.62.src.rpm", info.getSource());
        assertEquals("7.4.326", info.getVersion());
        assertEquals("x86_64", info.getArchitecture());
        assertEquals("RSA/SHA256, Wed 08 Oct 2014 03:45:49 PM UTC, Key ID 70af9e8139db7c82",
                info.getSignature());
        assertEquals("2.62", info.getRelease().get());
        assertEquals("SUSE LLC <https://www.suse.com/>", info.getVendor());
        assertEquals("https://www.suse.com/", info.getPackager());
        assertEquals("Vim (Vi IMproved) is an almost compatible version of the UNIX " +
                "editor\nvi. Almost every possible command can be performed using " +
                "only ASCII\ncharacters. Only the 'Q' command is missing (you do not" +
                " need it). Many\nnew features have been added: multilevel undo," +
                " command line history,\nfile name completion, block operations, and" +
                " editing of binary data.\n\nVi is available for the AMIGA, MS-DOS," +
                " Windows NT, and various versions\nof UNIX.\n\nFor SUSE Linux, Vim" +
                " is used as /usr/bin/vi.", info.getDescription());
        assertEquals("2714880", info.getSize());
    }
}
