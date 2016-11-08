package com.suse.salt.netapi.datatypes.target;

/**
 * Target for specifying minions by glob pattern.
 */
public class Glob implements Target<String>, SSHTarget<String> {

    public static final Glob ALL = new Glob("*");

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
    public String getTarget() {
        return glob;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return "glob";
    }
}
