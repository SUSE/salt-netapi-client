package com.suse.saltstack.netapi.datatypes.target;

/**
 * Target for specifying minions by glob pattern.
 */
public class Glob implements Target<String> {

    private final String glob;

    public Glob() {
        this("*");
    }

    public Glob(String glob) {
        this.glob = glob;
    }

    @Override
    public String target() {
        return glob;
    }

    @Override
    public String targetType() {
        return "glob";
    }
}
