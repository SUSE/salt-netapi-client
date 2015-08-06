package com.suse.saltstack.netapi.results;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Represents a SaltStack result.
 *
 * @param <T> The type of the value this result holds.
 */
public class Result<T> {

    @SerializedName("return")
    private T result;
    private List<Info> info;

    /**
     * Returns the value of this result.
     *
     * @return The value of this result.
     */
    public T getResult() {
        return result;
    }

    /**
     * Returns ancillary information about this result
     *
     * @return Ancillary information, or null if not available.
     */
    public List<Info> getInfo() {
        if (info == null || info.isEmpty())
            return null;

        return info;
    }
}
