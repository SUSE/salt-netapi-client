package com.suse.saltstack.netapi.client;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class SaltStackRunResults {

    @SerializedName("return")
    private List<Map<String, String>> results;

    public List<Map<String, String>> getResults() {
        return results;
    }
}
