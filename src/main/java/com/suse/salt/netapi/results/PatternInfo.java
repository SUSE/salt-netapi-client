package com.suse.salt.netapi.results;

/**
 * Information about a pattern as returned by "pkg.list_patterns".
 */
public class PatternInfo {
    private String summary;
    private boolean installed;

    public String getSummary() {
        return summary;
    }

    public boolean isInstalled() {
        return installed;
    }

    @Override
    public String toString() {
        return "{Summary: " + summary + ", installed: " + installed + "}";
    }
}
