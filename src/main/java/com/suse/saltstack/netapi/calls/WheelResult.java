package com.suse.saltstack.netapi.calls;

/**
 * Wrapper around the wheel functions return type.
 *
 * @param <R> the return type of the called function
 */
public class WheelResult<R> {

    private String tag;
    private Data<R> data;

    public Data<R> getData() {
        return data;
    }

    public String getTag() {
        return tag;
    }
}
