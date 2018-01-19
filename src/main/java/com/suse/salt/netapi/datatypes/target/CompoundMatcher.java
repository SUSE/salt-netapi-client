package com.suse.salt.netapi.datatypes.target;

/**
 *
 * Target for referencing compound matcher.
 */
public class CompoundMatcher implements Target<String> {

    private final String matcher;

    /**
     * Constructor expecting a matcher as string.
     *
     * @param matcher
     *            the compound matcher as string
     */
    public CompoundMatcher(String matcher) {
        this.matcher = matcher;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTarget() {
        return matcher;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return "compound";
    }
}

