package com.suse.saltstack.netapi.client;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class SaltStackJobResult {

    @SerializedName("return")
    private List<SaltStackJob> jobs;

    public List<SaltStackJob> getJobs() {
        return jobs;
    }
}
