package com.suse.salt.netapi.results;

import com.google.gson.JsonElement;

/**
 * Catch all error that contains the rest of the json which could not be parsed.
 */
public class GenericSaltError implements SaltError {

    private final JsonElement json;

    public GenericSaltError(JsonElement json) {
       this.json = json;
    }

    @Override
    public String toString() {
        return "GenericSaltError(" + json.toString() + ")";
    }
}
