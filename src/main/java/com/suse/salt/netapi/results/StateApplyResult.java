package com.suse.salt.netapi.results;

import com.google.gson.annotations.SerializedName;

/**
 * Result structure as returned by state.apply to be parsed from event data.
 *
 * @param <R> the parameterized type of the changes
 */
public class StateApplyResult<R> {

    private String comment;
    private Object name;
    @SerializedName("start_time")
    private String startTime;
    private boolean result;
    private double duration;
    @SerializedName("__run_num__")
    private int runNum;
    protected R changes;

    public String getComment() {
        return comment;
    }

    public String getName() {
        return name.toString();
    }

    public String getStartTime() {
        return startTime;
    }

    public boolean isResult() {
        return result;
    }

    public double getDuration() {
        return duration;
    }

    public int getRunNum() {
        return runNum;
    }

    public R getChanges() {
        return changes;
    }
}
