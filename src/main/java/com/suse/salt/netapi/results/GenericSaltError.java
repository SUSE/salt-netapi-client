package com.suse.salt.netapi.results;

import com.google.gson.JsonElement;

import java.util.function.Function;

/**
 * Catch all error that contains the rest of the json which could not be parsed.
 */
final public class GenericSaltError implements SaltError {

    private final JsonElement json;

    public GenericSaltError(JsonElement json) {
        this.json = json;
    }

    @Override
    public String toString() {
        return "GenericSaltError(" + json.toString() + ")";
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    public <T> T fold(Function<FunctionNotAvailable, ? extends T> fnNotAvail,
            Function<ModuleNotSupported, ? extends T> modNotSupported,
            Function<GenericSaltError, ? extends T> generic) {
        return generic.apply(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else {
            return obj instanceof GenericSaltError &&
                    ((GenericSaltError) obj).json.equals(json);
        }
    }
}
