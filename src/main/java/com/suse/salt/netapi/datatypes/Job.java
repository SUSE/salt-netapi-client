package com.suse.salt.netapi.datatypes;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.TimeZone;

/**
 * Representation of a previously run job.
 */
public class Job {

    @SerializedName("Function")
    private String function;

    @SerializedName("Target")
    private String target;

    @SerializedName("Target-type")
    private String targetType;

    @SerializedName("User")
    private String user;

    @SerializedName("Arguments")
    private Arguments arguments;

    @SerializedName("StartTime")
    private StartTime startTime;

    public String getFunction() {
        return function;
    }

    public String getTarget() {
        return target;
    }

    public String getTargetType() {
        return targetType;
    }

    public String getUser() {
        return user;
    }

    public Arguments getArguments() {
        return arguments;
    }

    /**
     * Returns start time at a given {@link TimeZone}
     *
     * @param tz TimeZone of the master associated with the Job
     * @return Date representation of the start time.
     */
    public Date getStartTime(TimeZone tz) {
        return startTime == null ? null : startTime.getDate(tz);
    }

    /**
     *  Returns start time assuming default {@link TimeZone} is Salt master's timezone.
     *
     * @return Date representation of the start time.
     */
    public Date getStartTime() {
        return startTime == null ? null : startTime.getDate();
    }
}
