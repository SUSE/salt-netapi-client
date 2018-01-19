package com.suse.salt.netapi.datatypes.target;

import java.util.Map;

/**
 * Target interface for specifying a group of minions.
 *
 * @param <T> Type of tgt property when making a request
 */
public interface Target<T> {

    /**
     * Return the target.
     *
     * @return the target
     */
    public T getTarget();

    /**
     * Return the target type.
     *
     * @return the target type
     */
    public TargetType getType();

    /**
     * Return the properties that belong in a request body.
     * This will include the `tgt` and `expr_form` properties.
     * and optionally the `delimiter` property.
     *
     * @return a map of property keys and values
     */
    public Map<String, Object> getProps();
}
