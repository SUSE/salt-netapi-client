package com.suse.salt.netapi.datatypes.target;

/**
 * Target for specifying minions by compound expression.
 */
public class Compound extends AbstractTarget<String> implements Target<String> {

    /**
     * Default constructor.
     */
    public Compound(String expression) { super(expression); }

    /**
     * {@inheritDoc}
     */
    @Override
    public TargetType getType() { return TargetType.COMPOUND; }
}
