package com.suse.salt.netapi.datatypes.target;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

abstract class DictionaryTarget extends AbstractTarget<String> {

    public final static char DEFAULT_DELIMITER = ':';

    protected final String key;
    protected final String value;
    protected final char delimiter;

    public DictionaryTarget(String target) {
        this(target, DEFAULT_DELIMITER);
    }

    public DictionaryTarget(String target, char delimiter) {
        super(target);
        this.delimiter = Objects.requireNonNull(delimiter);

        int pos = target.lastIndexOf(delimiter);
        if (pos < 1 || pos == target.length() - 1) {
            // The delimiter was not found, or was
            // found and the start or end of the string
            throw new InvalidParameterException();
        }
        this.key = target.substring(0, pos);
        this.value = target.substring(pos + 1);
    }

    public DictionaryTarget(String key, String value) {
        this(key, value, DEFAULT_DELIMITER);
    }

    public DictionaryTarget(String key, String value, char delimiter) {
        super(key + delimiter + value);
        this.key = Objects.requireNonNull(key);
        this.value = Objects.requireNonNull(value);
        this.delimiter = Objects.requireNonNull(delimiter);
    }

    /**
     * @return the grain name of this matcher
     */
    public String getKey() {
        return key;
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
    public Map<String, Object> getProps() {
        Map<String, Object> props = super.getProps();
        if (getDelimiter() != DEFAULT_DELIMITER) {
            props.put("delimiter", getDelimiter());
        }
        return props;
    }
}
