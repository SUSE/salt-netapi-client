package com.suse.salt.netapi.results;

import com.google.gson.annotations.SerializedName;

import java.util.function.Function;

/**
 * Represents a change from some old value to a new one.
 * primarily used in salt for showing package changes
 *
 * @param <T> type of the changed value
 */
public class Change<T> {

    @SerializedName("old")
    private final T oldValue;
    @SerializedName("new")
    private final T newValue;

    /**
     * constructor
     * @param oldValue old value
     * @param newValue new value
     */
    public Change(T oldValue, T newValue) {
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * the old value
     * @return old value
     */
    public T getOldValue() {
        return oldValue;
    }

    /**
     * the new value
     * @return new value
     */
    public T getNewValue() {
        return newValue;
    }

    /**
     * Applies a mapping function to both the old and the new value, wrapping the result
     * in a new Change.
     * @param fn the mapping function
     * @param <R> type returned by the mapping function
     * @return a new Change with mapped values
     */
    public <R> Change<R> map(Function<T, R> fn) {
        return new Change<>(
                fn.apply(getOldValue()),
                fn.apply(getNewValue())
        );
    }
}
