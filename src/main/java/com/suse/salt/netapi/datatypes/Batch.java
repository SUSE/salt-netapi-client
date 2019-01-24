package com.suse.salt.netapi.datatypes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A class representing the batch parameter. Salt uses a string for the batch parameter,
 * but it accept strings representing both exact numerals and percents, so this class
 * encapsulates the creation and usage of the batch strings, adding safety.
 */
public class Batch {
    // The actual batch string
    private String batch;
    private Optional<Double> delay = Optional.empty();

    private Batch(String batch) {
        this.batch = batch;
    }

    private Batch(String batch, Double delay) {
        this.batch = batch;
        this.delay = Optional.of(delay);
    }

    /**
     * Batch delay specifies the time for salt to wait for more minions to return a result before
     * scheduling the next batch. This helps to avoid single minion batches.
     *
     * @param seconds time to wait in seconds.
     * @return a copy of this batch configuration with the specified batch delay.
     */
    public Batch delayed(Double seconds) {
        return new Batch(batch, seconds);
    }

    @Override
    public String toString() {
        return batch;
    }

    public String getBatch() {
        return batch;
    }

    public Optional<Double> getDelay() {
        return delay;
    }

    public Map<String, Object> getParams() {
        Map<String, Object> customArgs = new HashMap<>();
        customArgs.put("batch", batch);
        delay.ifPresent(d -> {
            customArgs.put("batch_delay", d);
        });
        return customArgs;
    }

    /**
     * Construct a Batch from a value representing a percent
     * @param value the percent, which must be greater than 0 and less than or equal to 100
     * @return the Batch
     */
    public static Batch asPercent(int value) {
        if (value <= 0 || value > 100) {
            throw new IllegalArgumentException("Expected value greater than 0 and less " +
                    "than or equal to 100 to make valid batch as a percent.");
        }

        return new Batch(value + "%");
    }

    /**
     * Construct a Batch from a value representing an exact amount of items
     * @param value the exact amount of items, which must be greater than 0
     * @return the Batch
     */
    public static Batch asAmount(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Expected value greater than 0 to make a " +
                    "valid batch amount");
        }

        return new Batch(Integer.toString(value));
    }
}
