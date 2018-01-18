package com.suse.salt.netapi.datatypes.target;

/**
 * Target for specifying minions by range expression.
 */
public class Range extends AbstractTarget<String> implements Target<String> {

    /**
     * Creates a range matcher
     *
     * @param range Range targeting expression
     */
    public Range(String range) {
        super(TargetType.RANGE, range);
    }

}
