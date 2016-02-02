package com.suse.salt.netapi.results;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a SaltStack result.
 *
 * @param <T> The type of the value this result holds.
 */
public class Result<T> {

    @SerializedName("return")
    private T result;

    /**
     * Returns the value of this result.
     *
     * @return The value of this result.
     */
    public T getResult() {
        return result;
    }
}
