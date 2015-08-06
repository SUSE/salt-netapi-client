package com.suse.saltstack.netapi.results;

import java.util.HashSet;

import com.google.gson.annotations.SerializedName;

public class Info {

    @SerializedName("Minions")
    private final HashSet<String> minions = new HashSet<>();

    public HashSet<String> getMinions() {
        return minions;
    }
}
