package com.suse.salt.netapi.datatypes.target;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Base class for all target types
 *
 * @param <T> The data type of the target class
 */
abstract class AbstractTarget<T> {

    protected final T target;
    protected final TargetType type;

    protected AbstractTarget(TargetType type, T target) {
        this.target = Objects.requireNonNull(target);
        this.type = Objects.requireNonNull(type);
    }

    public T getTarget() {
        return target;
    }

    public TargetType getType() { return type; }

    /**
     * @return a map of items to include in the API call payload
     */
    public Map<String, Object> getProps() {
        Map<String, Object> props = new HashMap<>();
        props.put("tgt", getTarget());
        props.put("tgt_type", getType().getValue());
        return props;
    }
}
