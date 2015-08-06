package com.suse.saltstack.netapi.datatypes.target;

/**
 * Target for specifying minions by glob pattern.
 */
public class Glob implements Target<String> {

    private final String glob;

    /**
     * Default constructor.
     */
    public Glob() {
        this("*");
    }

    /**
     * Constructor expecting a glob pattern as string.
     *
     * @param glob glob pattern
     */
    public Glob(String glob) {
        this.glob = glob;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String target() {
        return glob;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String targetType() {
        return "glob";
    }
}
