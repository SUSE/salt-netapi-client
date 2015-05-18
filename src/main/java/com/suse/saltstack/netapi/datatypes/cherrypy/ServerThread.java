package com.suse.saltstack.netapi.datatypes.cherrypy;

import com.google.gson.annotations.SerializedName;

/**
 * Representation of server thread statistics.
 */
public class ServerThread {

    @SerializedName("Bytes Read")
    private int bytesRead;

    @SerializedName("Bytes Written")
    private int bytesWritten;

    @SerializedName("Read Throughput")
    private double readThroughput;

    @SerializedName("Requests")
    private int requests;

    @SerializedName("Work Time")
    private double workTime;

    @SerializedName("Write Throughput")
    private double writeThroughput;

    public double getWriteThroughput() {
        return writeThroughput;
    }

    public int getBytesRead() {
        return bytesRead;
    }

    public int getBytesWritten() {
        return bytesWritten;
    }

    public double getReadThroughput() {
        return readThroughput;
    }

    public int getRequests() {
        return requests;
    }

    public double getWorkTime() {
        return workTime;
    }
}
