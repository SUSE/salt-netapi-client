package com.suse.salt.netapi.calls;

/**
 * Result of calling a runner module function asynchronously.
 *
 * @param <R> the return type of the called function
 */
public class RunnerAsyncResult<R> extends ScheduledJob<R> {

    private String tag;

    public String getTag() {
        return tag;
    }
}
