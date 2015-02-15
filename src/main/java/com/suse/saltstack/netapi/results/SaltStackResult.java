package com.suse.saltstack.netapi.results;

import com.google.gson.annotations.SerializedName;

public class SaltStackResult<T> {

    @SerializedName("return")
    private T result;

    public T getResult() {
        return result;
    }
}
