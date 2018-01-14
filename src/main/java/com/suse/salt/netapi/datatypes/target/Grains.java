package com.suse.salt.netapi.datatypes.target;

/**
 * Matcher based on salt grains
 */
public class Grains extends DictionaryTarget implements Target<String> {

    /**
     * Creates a grains matcher
     *
     * @param target the targeting expression
     */
    public Grains(String target) {
        super(target);
    }

    /**
     * Creates a grains matcher
     *
     * @param target the targeting expression
     * @param delimiter the character to delimit nesting in the grain name
     */
    public Grains(String target, char delimiter) {
        super(target, delimiter);
    }

    /**
     * Creates a grains matcher
     *
     * @param grain the grain name
     * @param value the value to match
     */
    public Grains(String grain, String value) {
        super(grain, value);
    }

    /**
     * Creates a grains matcher
     *
     * @param grain the grain name
     * @param value the value to match
     * @param delimiter the character to delimit nesting in the grain name
     */
    public Grains(String grain, String value, char delimiter) {
        super(grain, value, delimiter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TargetType getType() { return TargetType.GRAIN; }

    /**
     * @return the grain identifier key
     *
     * @deprecated
     * Use {@link #getKey()} instead.
     */
    @Deprecated
    public String getGrain() { return getKey(); }
}
