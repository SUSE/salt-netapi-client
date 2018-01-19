package com.suse.salt.netapi.datatypes.target;

/**
 * Target for specifying minions by compound expression.
 */
public class Compound extends AbstractTarget<String> implements Target<String> {

    /**
     * Creates a compound matcher
     *
     * @param expression Compound targeting expression
     */
    public Compound(String expression) { super(TargetType.COMPOUND, expression); }

}
