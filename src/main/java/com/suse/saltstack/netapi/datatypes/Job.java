package com.suse.saltstack.netapi.datatypes;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.suse.saltstack.netapi.parser.JsonParser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Representation of a previously run job.
 */
public class Job {

    // StartTime example from API: "2015, Mar 04 19:28:29.724698"
    public static final SimpleDateFormat START_TIME_FORMAT =
            new SimpleDateFormat("yyyy, MMM dd HH:mm:ss.SSS", Locale.US);

    @SerializedName("Function")
    private String function;

    @SerializedName("Target")
    private String target;

    @SerializedName("Target-type")
    @JsonAdapter(JsonParser.TargetTypeAdapter.class)
    private String targetType;

    @SerializedName("User")
    private String user;

    @SerializedName("Arguments")
    private Arguments arguments;

    /**
     * Please note that start time will be in salt-master time zone
     */
    @SerializedName("StartTime")
    @JsonAdapter(JsonParser.JobStartTimeJsonAdapter.class)
    private Date startTime;

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

    public Date getStartTime() {
        return startTime;
    }
}
