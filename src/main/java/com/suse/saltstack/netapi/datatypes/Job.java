package com.suse.saltstack.netapi.datatypes;

import com.google.gson.annotations.SerializedName;

// TODO - handle dates too (they are represented in really
// exotic format: (2015, Mar 04 19:28:29.724698))

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
}
