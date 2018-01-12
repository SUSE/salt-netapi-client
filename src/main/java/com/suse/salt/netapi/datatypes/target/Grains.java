package com.suse.salt.netapi.datatypes.target;

import java.util.HashMap;
import java.util.Map;

/**
 * Matcher based on salt grains
 */
public class Grains extends AbstractTarget<String> implements Target<String> {

    private final String grain;
    private final String value;
    private final char delimiter;
    public final static char DEFAULT_DELIMITER = ':';

    /**
     * Creates a grains matcher
     *
     * @param grain the grain name
     * @param value the value to match
     */
    public Grains(String grain, String value) {
        this(grain, value, DEFAULT_DELIMITER);
    }

    /**
     * Creates a grains matcher
     *
     * @param grain the grain name
     * @param value the value to match
     * @param delimiter the character to delimit nesting in the grain name
     */
    public Grains(String grain, String value, char delimiter) {
        super(grain + delimiter + value);
        this.grain = grain;
        this.value = value;
        this.delimiter = delimiter;
    }

    /**
     * @return the grain name of this matcher
     */
    public String getGrain() {
        return grain;
    }

    /**
     * @return the value to match the grain
     */
    public String getValue() {
        return value;
    }

    /**
     * @return the delimiter used by this target
     */
    public char getDelimiter() {
        return delimiter;
    }

    @Override
    public TargetType getType() {
        return TargetType.GRAIN;
    }

    @Override
    public Map<String, Object> getProps() {
        Map<String, Object> props = new HashMap<>();
        props.put("tgt", getTarget());
        props.put("expr_form", getType().getValue());
        if (getDelimiter() != DEFAULT_DELIMITER) {
            props.put("delimiter", getDelimiter());
        }
        return props;
    }
}
