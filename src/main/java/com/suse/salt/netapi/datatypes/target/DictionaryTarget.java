package com.suse.salt.netapi.datatypes.target;

import java.security.InvalidParameterException;
import java.util.Map;
import java.util.Objects;

/**
 * Base class for all grain and pillar target types
 */
abstract class DictionaryTarget extends AbstractTarget<String> {

    public final static char DEFAULT_DELIMITER = ':';

    protected final String key;
    protected final String value;
    protected final char delimiter;

    DictionaryTarget(TargetType type, String target) {
        this(type, target, DEFAULT_DELIMITER);
    }

    DictionaryTarget(TargetType type, String target, char delimiter) {
        super(type, target);
        this.delimiter = delimiter;

        int pos = target.lastIndexOf(delimiter);
        if (pos < 1 || pos == target.length() - 1) {
            // The delimiter was not found, or was
            // found and the start or end of the string
            throw new InvalidParameterException();
        }
        this.key = target.substring(0, pos);
        this.value = target.substring(pos + 1);
    }

    DictionaryTarget(TargetType type, String key, String value) {
        this(type, key, value, DEFAULT_DELIMITER);
    }

    DictionaryTarget(TargetType type, String key, String value, char delimiter) {
        super(type, key + delimiter + value);
        this.key = Objects.requireNonNull(key);
        this.value = Objects.requireNonNull(value);
        this.delimiter = delimiter;
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

    /**
     * @return a map of items to include in the API call payload
     *         This will include the 'delimiter' key if not using the default delimiter.
     */
    @Override
    public Map<String, Object> getProps() {
        Map<String, Object> props = super.getProps();
        if (delimiter != DEFAULT_DELIMITER) {
            props.put("delimiter", getDelimiter());
        }
        return props;
    }
}
