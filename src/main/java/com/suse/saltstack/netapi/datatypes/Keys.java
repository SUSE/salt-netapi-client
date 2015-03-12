package com.suse.saltstack.netapi.datatypes;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Saltstack Keys information.
 */
public class Keys {

    private List<String> local;

    private List<String> minions;

    @SerializedName("minions_pre")
    private List<String> unacceptedMinions;

    @SerializedName("minions_rejected")
    private List<String> rejectedMinions;

    public List<String> getLocal() {
        return local;
    }

    public List<String> getMinions() {
        return minions;
    }

    public List<String> getUnacceptedMinions() {
        return unacceptedMinions;
    }

    public List<String> getRejectedMinions() {
        return rejectedMinions;
    }
}
