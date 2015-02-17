package com.suse.saltstack.netapi.results;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

/**
 * Models the netapi response from the /run endpoint
 */

public class SaltStackRunResults {

    @SerializedName("return")
    private List<Map<String, String>> results;

    /**
     * @return A Map of minion names and command results for each minion,
     *         wrapped in a list with one element.
     */
    public List<Map<String, String>> getResults() {
        return results;
    }
}
