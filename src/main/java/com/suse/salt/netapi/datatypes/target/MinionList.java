package com.suse.salt.netapi.datatypes.target;

import java.util.Arrays;
import java.util.List;

/**
 * Target for specifying a list of minions.
 */
public class MinionList extends AbstractTarget<List<String>> implements Target<List<String>>, SSHTarget<List<String>> {

    /**
     * Constructor taking a list of minions as strings.
     *
     * @param targets as a list of strings
     */
    public MinionList(List<String> targets) {
        super(TargetType.LIST, targets);
    }

    /**
     * Constructor taking an optional list of strings.
     *
     * @param targets as strings
     */
    public MinionList(String... targets) {
        super(TargetType.LIST, Arrays.asList(targets));
    }

}
