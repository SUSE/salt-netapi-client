package com.suse.salt.netapi.results;

/**
 * Wrapper object representing a "ret" element to be parsed from event data.
 *
 * @param <T> the type that is wrapped
 */
public class Ret<T> {

    private T ret;

    public T getRet() {
        return ret;
    }
}
