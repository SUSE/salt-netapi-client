package com.suse.salt.netapi.datatypes;

import com.suse.salt.netapi.AuthModule;

/**
 * Datatype for holding all data needed for password authentication
 */
public class PasswordAuth {

    private final String username;
    private final String password;
    private final AuthModule module;

    public PasswordAuth(String username, String password, AuthModule module) {
        this.username = username;
        this.password = password;
        this.module = module;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public AuthModule getModule() {
        return module;
    }

    @Override
    public String toString() {
        return "PasswordAuth(username = " + username +
               ", password = <REDACTED>, authModule = " + module.getValue() + ")";
    }
}
