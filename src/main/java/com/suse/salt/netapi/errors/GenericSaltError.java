package com.suse.salt.netapi.errors;

import com.google.gson.JsonElement;

import java.util.function.Function;

/**
 * Catch all error that contains the rest of the json which could not be parsed.
 */
final public class GenericSaltError implements SaltError {

    private final JsonElement json;
    private final Throwable throwable;

    public GenericSaltError(JsonElement json, Throwable throwable) {
        this.json = json;
        this.throwable = throwable;
    }

    public JsonElement getJson() {
        return json;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public <T> T fold(Function<FunctionNotAvailable, ? extends T> fnNotAvail,
            Function<ModuleNotSupported, ? extends T> modNotSupported,
            Function<GenericSaltError, ? extends T> generic) {
        return generic.apply(this);
    }

    @Override
    public String toString() {
        return "GenericSaltError(" + json.toString() + ", " + throwable.getMessage() + ")";
    }

}
