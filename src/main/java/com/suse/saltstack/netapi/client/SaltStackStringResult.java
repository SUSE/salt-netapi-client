package com.suse.saltstack.netapi.client;

import com.google.gson.annotations.SerializedName;

public class SaltStackStringResult {

    @SerializedName("return")
    private String result;

    public String getResult() {
        return result;
    }
}
