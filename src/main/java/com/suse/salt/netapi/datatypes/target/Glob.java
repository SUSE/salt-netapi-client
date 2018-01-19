package com.suse.salt.netapi.datatypes.target;

/**
 * Target for specifying minions by glob pattern.
 */
public class Glob extends AbstractTarget<String> implements Target<String>, SSHTarget<String> {

    public static final Glob ALL = new Glob("*");

    /**
     * Creates a glob matcher
     */
    public Glob() { super(TargetType.GLOB, "*"); }

    /**
     * Creates a glob matcher
     *
     * @param glob Glob expression
     */
    public Glob(String glob) { super(TargetType.GLOB, glob); }

}
