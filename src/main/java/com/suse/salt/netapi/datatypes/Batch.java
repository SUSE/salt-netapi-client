package com.suse.salt.netapi.datatypes;

/**
 * A class representing the batch parameter. Salt uses a string for the batch parameter,
 * but it accept strings representing both exact numerals and percents, so this class
 * encapsulates the creation and usage of the batch strings, adding safety.
 */
public class Batch {
    // The actual batch string
    private String batch;

    private Batch(String batch) {
        this.batch = batch;
    }

    @Override
    public String toString() {
        return batch;
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

        return new Batch(Integer.toString(value) + "%");
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
