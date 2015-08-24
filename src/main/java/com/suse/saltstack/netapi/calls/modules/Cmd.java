package com.suse.saltstack.netapi.calls.modules;

import com.google.gson.reflect.TypeToken;
import com.suse.saltstack.netapi.calls.LocalCall;

import java.util.LinkedHashMap;
import java.util.Optional;

/**
 * salt.modules.cmdmod
 */
public class Cmd {

    private Cmd() { }

    public static LocalCall<String> run(String cmd) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("cmd", cmd);
        return new LocalCall<>("cmd.run", Optional.empty(), Optional.of(args),
                new TypeToken<String>(){});
    }
}
