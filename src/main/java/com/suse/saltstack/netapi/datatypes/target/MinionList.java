package com.suse.saltstack.netapi.datatypes.target;

import java.util.Arrays;
import java.util.List;

/**
 * Target for specifying a list of minions.
 */
public class MinionList implements Target<List<String>> {

    private final java.util.List<String> targets;

    public MinionList(List<String> targets) {
        this.targets = targets;
    }

    public MinionList(String... targets) {
        this(Arrays.asList(targets));
    }

    @Override
    public List<String> target() {
        return targets;
    }

    @Override
    public String targetType() {
        return "list";
    }
}
