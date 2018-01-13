package com.suse.salt.netapi.datatypes.target;

/**
 * Target for specifying minions by range expression.
 */
public class Range extends AbstractTarget<String> implements Target<String> {

    /**
     * Default constructor.
     */
    public Range(String expression) {
        super(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TargetType getType() { return TargetType.RANGE; }
}
