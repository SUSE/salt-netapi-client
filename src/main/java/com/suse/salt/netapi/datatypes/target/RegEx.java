package com.suse.salt.netapi.datatypes.target;

/**
 * Target for specifying minions by regular expression.
 */
public class RegEx extends AbstractTarget<String> implements Target<String>, SSHTarget<String> {

    /**
     * Creates a regular expression matcher
     *
     * @param regex Regular expression
     */
    public RegEx(String regex) {
        super(TargetType.PCRE, regex);
    }

}
