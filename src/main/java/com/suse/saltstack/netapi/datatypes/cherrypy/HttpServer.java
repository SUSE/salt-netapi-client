package com.suse.saltstack.netapi.datatypes.cherrypy;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Representation of HTTP server statistics.
 */
public class HttpServer {

    @SerializedName("Bytes Read")
    private int bytesRead;

    @SerializedName("Accepts/sec")
    private double acceptsPerSecond;

    @SerializedName("Socket Errors")
    private int socketErrors;

    @SerializedName("Accepts")
    private int accepts;

    @SerializedName("Threads Idle")
    private int threadsIdle;

    @SerializedName("Enabled")
    private boolean enable;

    @SerializedName("Bind Address")
    private String bindAddress;

    @SerializedName("Read Throughput")
    private int readThroughput;

    @SerializedName("Queue")
    private int queue;

    @SerializedName("Run time")
    private int runTime;

    @SerializedName("Worker Threads")
    private Map<String, ServerThread> workerThreads;

    @SerializedName("Threads")
    private int threads;

    @SerializedName("Bytes Written")
    private int bytesWritten;

    @SerializedName("Requests")
    private int requests;

    @SerializedName("Work Time")
    private int workTime;

    @SerializedName("Write Throughput")
    private double writeThroughput;

    public int getBytesRead() {
        return bytesRead;
    }

    public double getAcceptsPerSecond() {
        return acceptsPerSecond;
    }

    public int getSocketErrors() {
        return socketErrors;
    }

    public int getAccepts() {
        return accepts;
    }

    public int getThreadsIdle() {
        return threadsIdle;
    }

    public boolean isEnable() {
        return enable;
    }

    public String getBindAddress() {
        return bindAddress;
    }

    public int getReadThroughput() {
        return readThroughput;
    }

    public int getQueue() {
        return queue;
    }

    public int getRunTime() {
        return runTime;
    }

    public Map<String, ServerThread> getWorkerThreads() {
        return workerThreads;
    }

    public int getThreads() {
        return threads;
    }

    public int getBytesWritten() {
        return bytesWritten;
    }

    public int getRequests() {
        return requests;
    }

    public int getWorkTime() {
        return workTime;
    }

    public double getWriteThroughput() {
        return writeThroughput;
    }
}
