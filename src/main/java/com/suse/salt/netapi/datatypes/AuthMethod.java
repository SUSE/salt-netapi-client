package com.suse.salt.netapi.datatypes;

import com.suse.salt.netapi.utils.Xor;

/**
 * Datatype holding data for authentication which can either be password based or token based.
 */
public class AuthMethod {

    private final Xor<Token, PasswordAuth> internal;

    public AuthMethod(Token token) {
        internal = Xor.left(token);
    }

    public AuthMethod(PasswordAuth pwauth) {
        internal = Xor.right(pwauth);
    }

    public Xor<Token, PasswordAuth> getInternal() {
        return internal;
    }
}
