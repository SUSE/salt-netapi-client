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
        super(TargetType.GRAIN, target);
    }

    /**
     * Creates a grains matcher
     *
     * @param target the targeting expression
     * @param delimiter the character to delimit nesting in the grain name
     */
    public Grains(String target, char delimiter) {
        super(TargetType.GRAIN, target, delimiter);
    }

    /**
     * Creates a grains matcher
     *
     * @param grain the grain name
     * @param value the value to match
     */
    public Grains(String grain, String value) {
        super(TargetType.GRAIN, grain, value);
    }

    /**
     * Creates a grains matcher
     *
     * @param grain the grain name
     * @param value the value to match
     * @param delimiter the character to delimit nesting in the grain name
     */
    public Grains(String grain, String value, char delimiter) {
        super(TargetType.GRAIN, grain, value, delimiter);
    }

    /**
     * @return the grain identifier key
     *
     * @deprecated
     * Use {@link DictionaryTarget#getKey()} instead.
     */
    @Deprecated
    public String getGrain() { return getKey(); }
}
