package com.suse.salt.netapi.datatypes.target;

import java.util.Arrays;
import java.util.List;

/**
 * Target for specifying a list of minions.
 */
public class MinionList implements Target<List<String>>, SSHTarget<List<String>> {

    private final List<String> targets;

    /**
     * Constructor taking a list of minions as strings.
     *
     * @param targets as a list of strings
     */
    public MinionList(List<String> targets) {
        this.targets = targets;
    }

    /**
     * Constructor taking an optional list of strings.
     *
     * @param targets as strings
     */
    public MinionList(String... targets) {
        this(Arrays.asList(targets));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getTarget() {
        return targets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return "list";
    }
}
