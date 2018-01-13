package com.suse.salt.netapi.datatypes.target;

/**
 * Matcher based on salt pillar with regular expression matching
 */
public class PillarRegEx extends DictionaryTarget implements Target<String> {

    /**
     * Creates a pillar matcher
     *
     * @param target the targeting expression
     */
    public PillarRegEx(String target) {
        super(target);
    }

    /**
     * Creates a pillar matcher
     *
     * @param target the targeting expression
     * @param delimiter the character to delimit nesting in the grain name
     */
    public PillarRegEx(String target, char delimiter) {
        super(target, delimiter);
    }

    /**
     * Creates a pillar matcher
     *
     * @param pillar the pillar name
     * @param regex the regular expression to match
     */
    public PillarRegEx(String pillar, String regex) {
        super(pillar, regex);
    }

    /**
     * Creates a pillar matcher
     *
     * @param pillar the pillar name
     * @param regex the regular expression to match
     * @param delimiter the character to delimit nesting in the pillar name
     */
    public PillarRegEx(String pillar, String regex, char delimiter) {
        super(pillar, regex, delimiter);
    }

    @Override
    public TargetType getType() {
        return TargetType.PILLAR_PCRE;
    }
}
