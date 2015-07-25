package com.suse.saltstack.netapi.datatypes.target;

/**
 * Target interface for specifying a group of minions.
 *
 * @param <T> Type of tgt property when making a request
 */
public interface Target<T> {

    T target();

    String targetType();

}

