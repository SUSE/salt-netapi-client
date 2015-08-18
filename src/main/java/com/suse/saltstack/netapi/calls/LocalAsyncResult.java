package com.suse.saltstack.netapi.calls;

import java.util.List;

/**
 * Result of calling a execution module function asynchronously.
 *
 * @param <R> the return type of the called function
 */
public class LocalAsyncResult<R> extends ScheduledJob<R> {

    private List<String> minions;

    public List<String> getMinions() {
        return minions;
    }
}
