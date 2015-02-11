package com.suse.saltstack.netapi.results;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class SaltStackTokenResult {

    @SerializedName("return")
    List<SaltStackToken> result;

    public List<SaltStackToken> getResult() {
        return result;
    }
}
