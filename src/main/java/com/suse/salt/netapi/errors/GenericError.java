package com.suse.salt.netapi.errors;

import java.util.function.Function;

/**
 * Catch all error that contains only a error messages.
 *
 */
final public class GenericError implements SaltError {

    private final String message;

    public GenericError(String message) {
        this.message = message;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    public <T> T fold(Function<FunctionNotAvailable, ? extends T> fnNotAvail,
            Function<ModuleNotSupported, ? extends T> modNotSupported,
            Function<JsonParsingError, ? extends T> jsonError,
            Function<GenericError, ? extends T> generic,
            Function<SaltSSHError, ? extends T> saltSSHError
    ) {
        return generic.apply(this);
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "GenericError(" + message + ")";
    }

}
