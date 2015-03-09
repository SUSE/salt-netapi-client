package com.suse.saltstack.netapi.datatypes.cherrypy;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.Map;

public class Applications {

    @SerializedName("Uptime")
    private double uptime;

    @SerializedName("Bytes Read/Second")
    private double readsPerSecond;

    @SerializedName("Current Time")
    private Date currentTime;

    @SerializedName("Server Version")
    private String serverVersion;

    @SerializedName("Total Time")
    private double totalTime;

    @SerializedName("Enabled")
    private boolean enabled;

    @SerializedName("Start Time")
    private Date startTime;

    @SerializedName("Bytes Written/Second")
    private double writesPerSecond;

    @SerializedName("Total Bytes Read")
    private int totalBytesRead;

    @SerializedName("Current Requests")
    private int currentRequests;

    @SerializedName("Total Requests")
    private int totalRequests;

    @SerializedName("Requests")
    Map<String, Request> requests;

    @SerializedName("Bytes Read/Request")
    private double readsPerRequest;

    @SerializedName("Total Bytes Written")
    private int totalBytesWritten;

    @SerializedName("Requests/Second")
    private double requestsPerSecond;

    @SerializedName("Bytes Written/Request")
    private double writesPerRequest;

    public double getUptime() {
        return uptime;
    }

    public double getReadsPerSecond() {
        return readsPerSecond;
    }

    public Date getCurrentTime() {
        return currentTime;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Date getStartTime() {
        return startTime;
    }

    public double getWritesPerSecond() {
        return writesPerSecond;
    }

    public int getTotalBytesRead() {
        return totalBytesRead;
    }

    public int getCurrentRequests() {
        return currentRequests;
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public Map<String, Request> getRequests() {
        return requests;
    }

    public double getReadsPerRequest() {
        return readsPerRequest;
    }

    public int getTotalBytesWritten() {
        return totalBytesWritten;
    }

    public double getRequestsPerSecond() {
        return requestsPerSecond;
    }

    public double getWritesPerRequest() {
        return writesPerRequest;
    }
}
