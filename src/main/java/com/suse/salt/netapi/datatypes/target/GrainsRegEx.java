package com.suse.salt.netapi.datatypes.target;

/**
 * Matcher based on salt grains
 */
public class GrainsRegEx extends DictionaryTarget implements Target<String> {

    /**
     * Creates a grains matcher
     *
     * @param target the targeting expression
     */
    public GrainsRegEx(String target) {
        super(target);
    }

    /**
     * Creates a grains matcher
     *
     * @param target the targeting expression
     * @param delimiter the character to delimit nesting in the grain name
     */
    public GrainsRegEx(String target, char delimiter) {
        super(target, delimiter);
    }

    /**
     * Creates a grains matcher
     *
     * @param grain the grain name
     * @param regex the regular expression to match
     */
    public GrainsRegEx(String grain, String regex) {
        super(grain, regex);
    }

    /**
     * Creates a grains matcher
     *
     * @param grain the grain name
     * @param regex the regular expression to match
     * @param delimiter the character to delimit nesting in the grain name
     */
    public GrainsRegEx(String grain, String regex, char delimiter) {
        super(grain, regex, delimiter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TargetType getType() {
        return TargetType.GRAIN_PCRE;
    }
}
