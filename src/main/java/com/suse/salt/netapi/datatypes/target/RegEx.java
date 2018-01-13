package com.suse.salt.netapi.datatypes.target;

/**
 * Target for specifying minions by regular expression.
 */
public class RegEx extends AbstractTarget<String> implements Target<String>, SSHTarget<String> {

    /**
     * Default constructor.
     */
    public RegEx(String expression) {
        super(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TargetType getType() {
        return TargetType.PCRE;
    }
}
