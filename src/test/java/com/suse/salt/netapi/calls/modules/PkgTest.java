package com.suse.salt.netapi.calls.modules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.google.gson.reflect.TypeToken;
import com.suse.salt.netapi.calls.modules.Pkg.Info;
import com.suse.salt.netapi.parser.JsonParser;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.utils.Xor;

import org.junit.Test;

import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Pkg unit tests.
 */
public class PkgTest {

    @Test
    public void testInfoInstalled() {
        TypeToken<Map<String, Pkg.Info>> type = Pkg.infoInstalled(
                new ArrayList<>(), false, "vim").getReturnType();
        InputStream is = this.getClass()
                .getResourceAsStream("/modules/pkg/info_installed.json");
        JsonParser<Map<String, Pkg.Info>> parser = new JsonParser<>(type);
        Map<String, Pkg.Info> parsed = parser.parse(is);
        Pkg.Info info = parsed.get("vim");

        assertEquals(ZonedDateTime.of(2014, 10, 8, 3, 44, 36, 0, ZoneId.of("Z")),
                info.getBuildDate().get());
        assertEquals("Productivity/Editors/Vi", info.getGroup().get());
        assertEquals("Vim", info.getLicense().get());
        assertEquals("sheep21", info.getBuildHost().get());
        assertEquals("http://www.vim.org/", info.getUrl().get());
        assertEquals("(not relocatable)", info.getRelocations().get());
        assertEquals(ZonedDateTime.of(2015, 8, 24, 5, 58, 22, 0, ZoneId.of("Z")),
                info.getInstallDate().get());
        assertEquals("Vi IMproved", info.getSummary().get());
        assertEquals("vim-7.4.326-2.62.src.rpm", info.getSource().get());
        assertEquals("7.4.326", info.getVersion().get());
        assertEquals("x86_64", info.getArchitecture().get());
        assertEquals("RSA/SHA256, Wed 08 Oct 2014 03:45:49 PM UTC, Key ID 70af9e8139db7c82",
                info.getSignature().get());
        assertEquals("2.62", info.getRelease().get());
        assertEquals("SUSE LLC <https://www.suse.com/>", info.getVendor().get());
        assertEquals("https://www.suse.com/", info.getPackager().get());
        assertEquals("Vim (Vi IMproved) is an almost compatible version of the UNIX " +
                "editor\nvi. Almost every possible command can be performed using " +
                "only ASCII\ncharacters. Only the 'Q' command is missing (you do not" +
                " need it). Many\nnew features have been added: multilevel undo," +
                " command line history,\nfile name completion, block operations, and" +
                " editing of binary data.\n\nVi is available for the AMIGA, MS-DOS," +
                " Windows NT, and various versions\nof UNIX.\n\nFor SUSE Linux, Vim" +
                " is used as /usr/bin/vi.", info.getDescription().get());
        assertEquals("2714880", info.getSize().get());
    }

    @Test
    public void testUpgradeAvailable() {
        TypeToken<Boolean> type = Pkg.upgradeAvailable("cloud-init").getReturnType();
        InputStream is = this.getClass()
                .getResourceAsStream("/modules/pkg/upgrade_available.json");
        JsonParser<Boolean> parser = new JsonParser<>(type);
        Boolean parsed = parser.parse(is);
        assertFalse(parsed);
    }

    @Test
    public void testLatestVersionSinglePackage() {
        TypeToken<String> type = Pkg.latestVersion("tinc").getReturnType();
        InputStream is = this.getClass()
                .getResourceAsStream("/modules/pkg/latest_version_single.json");
        JsonParser<String> parser = new JsonParser<>(type);
        String parsed = parser.parse(is);
        assertEquals("1.0.23-2", parsed);
    }

    @Test
    public void testLatestVersionsMultiplePackages() {
        TypeToken<Map<String, String>> type = Pkg.latestVersion(
                "openvpn", "weechat", "tmux").getReturnType();
        InputStream is = this.getClass()
                .getResourceAsStream("/modules/pkg/latest_version_multiple.json");
        JsonParser<Map<String, String>> parser = new JsonParser<>(type);
        Map<String, String> parsed = parser.parse(is);
        assertEquals("1.4.6-1ubuntu3.3", parsed.get("nginx"));
        assertEquals("0.4.2-3", parsed.get("weechat"));
        assertEquals("2.3.2-7ubuntu3.1", parsed.get("openvpn"));
        assertEquals(null, parsed.get("tmux")); // already at latest version
    }

    @Test
    public void testListPkgsMinimal() {
        TypeToken<Map<String, List<Xor<String, Info>>>> type =
                Pkg.listPkgs(new ArrayList<String>()).getReturnType();
        InputStream is = this.getClass()
                .getResourceAsStream("/modules/pkg/list_pkgs_minimal.json");
        JsonParser<Map<String, List<Xor<String, Info>>>> parser = new JsonParser<>(type);
        Map<String, List<Xor<String, Info>>> parsed = parser.parse(is);
        assertEquals(Stream.of(Xor.left("10.0.2-90.17")).collect(toList()),
                parsed.get("Mesa-libGL1"));
    }

    @Test
    public void testListPkgsFull() {
        TypeToken<Map<String, List<Xor<String, Info>>>> type =
                Pkg.listPkgs(new ArrayList<String>()).getReturnType();
        InputStream is = this.getClass()
                .getResourceAsStream("/modules/pkg/list_pkgs_full.json");
        JsonParser<Map<String, List<Xor<String, Info>>>> parser = new JsonParser<>(type);
        Map<String, List<Xor<String, Info>>> parsed = parser.parse(is);

        Info actual = parsed.get("Mesa-libGL1").get(0).right().get();

        assertEquals(Optional.of("x86_64"), actual.getArchitecture());
        assertEquals(Optional.of(1498555135L), actual.getInstallDateUnixTime());
        assertEquals(Optional.of("10.0.2-90.17"), actual.getVersion());
    }
}
