package com.suse.saltstack.netapi.calls;

import java.util.Map;

/**
 * Interface for all functions calls in salt.
 * @param <R> the return type of the called function
 */
public interface Call<R> {

    Map<String, Object> payload();

}
