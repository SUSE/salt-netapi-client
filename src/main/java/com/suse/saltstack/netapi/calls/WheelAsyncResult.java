package com.suse.saltstack.netapi.calls;


/**
 * Result of calling a wheel module function asynchronously
 * @param <R> the return type of the called function
 */
public class WheelAsyncResult<R> extends ScheduledJob<R> {

    private String tag;

    public String getTag() {
        return tag;
    }
}
