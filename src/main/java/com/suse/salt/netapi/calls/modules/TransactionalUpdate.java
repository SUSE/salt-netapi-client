package com.suse.salt.netapi.calls.modules;

import com.suse.salt.netapi.calls.LocalCall;

import com.google.gson.reflect.TypeToken;

import java.util.Optional;

/**
 * salt.modules.transactional_update
 *
 * https://docs.saltproject.io/en/latest/ref/modules/all/salt.modules.transactional_update.html
 */
public class TransactionalUpdate {
    private TransactionalUpdate() { }

    public static LocalCall<String> reboot() {
        return new LocalCall<>("transactional_update.reboot", Optional.empty(), Optional.empty(),
                new TypeToken<String>() {
                });
    }
}
