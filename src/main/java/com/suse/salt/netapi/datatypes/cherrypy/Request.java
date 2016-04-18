package com.suse.salt.netapi.datatypes.cherrypy;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.Optional;

/**
 * Representation of request statistics.
 */
public class Request {

    @SerializedName("Bytes Read")
    private Optional<Integer> bytesRead;

    @SerializedName("Bytes Written")
    private Optional<Integer> bytesWritten;

    @SerializedName("Response Status")
    private Optional<String> responeStatus;

    @SerializedName("Start Time")
    private Date startTime;

    @SerializedName("End Time")
    private Optional<Date> endTime;

    @SerializedName("Client")
    private String client;

    @SerializedName("Processing Time")
    private double processingTime;

    @SerializedName("Request-Line")
    private String requestLine;

    public Optional<Integer> getBytesRead() {
        return bytesRead;
    }

    public Optional<Integer> getBytesWritten() {
        return bytesWritten;
    }

    public Optional<String> getResponeStatus() {
        return responeStatus;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Optional<Date> getEndTime() {
        return endTime;
    }

    public String getClient() {
        return client;
    }

    public double getProcessingTime() {
        return processingTime;
    }

    public String getRequestLine() {
        return requestLine;
    }
}
