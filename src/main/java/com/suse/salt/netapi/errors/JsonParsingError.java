package com.suse.salt.netapi.errors;

import com.google.gson.JsonElement;

import java.util.function.Function;

/**
 * Json parsing error that contains the rest of the json which could not be parsed.
 */
final public class JsonParsingError implements SaltError {

    private final JsonElement json;
    private final Throwable throwable;

    public JsonParsingError(JsonElement json, Throwable throwable) {
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
            Function<JsonParsingError, ? extends T> jsonError,
            Function<GenericError, ? extends T> generic
    ) {
        return jsonError.apply(this);
    }

    @Override
    public String toString() {
        return "JsonParsingError(" + json.toString() + ", " + throwable.getMessage() + ")";
    }

}
