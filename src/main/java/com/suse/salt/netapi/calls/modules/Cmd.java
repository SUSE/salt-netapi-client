package com.suse.salt.netapi.calls.modules;

import com.suse.salt.netapi.calls.LocalCall;
import com.suse.salt.netapi.results.CmdResult;

import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * salt.modules.cmdmod
 */
public class Cmd {

    private Cmd() { }

    public static LocalCall<CmdResult> execCodeAll(String lang, String code) {
        return new LocalCall<>("cmd.exec_code_all", Optional.of(Arrays.asList(lang, code)),
                Optional.of(Map.of()), new TypeToken<CmdResult>(){});
    }

    public static LocalCall<String> run(String cmd) {
        return new LocalCall<>("cmd.run", Optional.empty(), Optional.of(Map.of("cmd", cmd)),
                new TypeToken<String>(){});
    }

    public static LocalCall<CmdResult> runAll(String cmd) {
        return new LocalCall<>("cmd.run_all", Optional.empty(), Optional.of(Map.of("cmd", cmd)),
                new TypeToken<CmdResult>(){});
    }

    public static LocalCall<String> execCode(String lang, String code) {
        return new LocalCall<>("cmd.exec_code", Optional.of(Arrays.asList(lang, code)),
                Optional.of(Map.of()), new TypeToken<String>(){});
    }

    public static LocalCall<Boolean> hasExec(String cmd) {
        return new LocalCall<>("cmd.has_exec", Optional.empty(), Optional.of(Map.of("cmd", cmd)),
                new TypeToken<Boolean>(){});
    }

    public static LocalCall<CmdResult> script(String source) {
        return new LocalCall<>("cmd.script", Optional.of(List.of(source)), Optional.of(Map.of()),
                new TypeToken<CmdResult>(){});
    }

    public static LocalCall<Integer> scriptRetcode(String source) {
        return new LocalCall<>("cmd.script_retcode", Optional.of(List.of(source)), Optional.of(Map.of()),
                new TypeToken<Integer>(){});
    }
}
