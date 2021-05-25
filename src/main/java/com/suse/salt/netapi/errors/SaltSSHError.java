package com.suse.salt.netapi.errors;

import java.util.function.Function;

/**
 * Interface for salt ssh specific errors
 */
public class SaltSSHError implements SaltError {

    private final int retcode;
    private final String message;

    public SaltSSHError(int retcode, String message) {
        this.message = message;
        this.retcode = retcode;
    }

    @Override
    public String toString() {
        return "SaltSSHError(" + getRetcode() + ", " + getMessage() + ")";
    }

    public int getRetcode() {
        return retcode;
    }

    public String getMessage() {
        return message;
    }

    public <T> T fold(Function<FunctionNotAvailable, ? extends T> fnNotAvail,
                      Function<ModuleNotSupported, ? extends T> modNotSupported,
                      Function<JsonParsingError, ? extends T> jsonError,
                      Function<GenericError, ? extends T> generic,
                      Function<SaltSSHError, ? extends T> saltSSHError) {
        return saltSSHError.apply(this);
    }

}

