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

    protected AbstractTarget(T target) {
        this.target = Objects.requireNonNull(target);
    }

    public T getTarget() {
        return target;
    }

    abstract TargetType getType();

    /**
     * @return a map of items to include in the API call payload
     */
    public Map<String, Object> getProps() {
        Map<String, Object> props = new HashMap<>();
        props.put("tgt", getTarget());
        props.put("expr_form", getType().getValue());
        return props;
    }
}
