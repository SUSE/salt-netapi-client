package com.suse.salt.netapi.datatypes.target;

/**
 * Target for specifying minions by glob pattern.
 */
public class Glob extends AbstractTarget<String> implements Target<String>, SSHTarget<String> {

    public static final Glob ALL = new Glob("*");

    /**
     * Default constructor.
     */
    public Glob() {
        super("*");
    }

    public Glob(String glob) {
        super(glob);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TargetType getType() {
        return TargetType.GLOB;
    }
}
