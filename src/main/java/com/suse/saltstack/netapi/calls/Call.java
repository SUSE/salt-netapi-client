package com.suse.saltstack.netapi.calls;

import java.util.Map;

/**
 * Interface for all function calls in salt.
 *
 * @param <R> the return type of the called function
 */
public interface Call<R> {

    /**
     * Return the call payload as a map of key/value pairs. Usually this contains the
     * function name and arguments, but the various clients differ in the details.
     *
     * @return call payload as a map of key/value pairs
     */
    Map<String, Object> payload();
}
