package com.suse.saltstack.netapi.datatypes.cherrypy;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Request {

    @SerializedName("Bytes Read")
    private Integer bytesRead;

    @SerializedName("Bytes Written")
    private Integer bytesWritten;

    @SerializedName("Response Status")
    private String responeStatus;

    @SerializedName("Start Time")
    private Date startTime;

    @SerializedName("End Time")
    private Date endTime;

    @SerializedName("Client")
    private String client;

    @SerializedName("Processing Time")
    private double processingTime;

    @SerializedName("Request-Line")
    private String requestLine;

    public Integer getBytesRead() {
        return bytesRead;
    }

    public Integer getBytesWritten() {
        return bytesWritten;
    }

    public String getResponeStatus() {
        return responeStatus;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
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
