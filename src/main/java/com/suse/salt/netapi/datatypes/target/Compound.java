package com.suse.salt.netapi.datatypes.target;

/**
 * Target for specifying minions by compound expression.
 */
public class Compound extends AbstractTarget<String> implements Target<String> {

    /**
     * Default constructor.
     */
    public Compound(String expression) {
        super(TargetType.COMPOUND, expression);
    }

}
