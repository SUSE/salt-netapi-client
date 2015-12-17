package com.suse.saltstack.netapi.calls.modules;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import com.google.gson.reflect.TypeToken;
import com.suse.saltstack.netapi.calls.LocalCall;

/**
 * salt.modules.saltutil
 */
public class SaltUtil {

    public static LocalCall<List<String>> syncGrains() {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        return new LocalCall("saltutil.sync_grains", Optional.empty(),
                Optional.of(args), new TypeToken<List<String>>() {
                });
    }

}
