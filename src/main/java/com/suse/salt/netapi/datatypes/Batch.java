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
    private Optional<Double> delay;
    private Optional<Integer> presencePingTimeout;

    private Batch(BatchBuilder builder) {
        this.batch = builder.batch;
        this.delay = builder.delay;
        this.presencePingTimeout = builder.presencePingTimeout;
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

    public Optional<Integer> getPresencePingTimeout() {
        return presencePingTimeout;
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
        return Batch.custom().withBatchAsPercent(value).build();
    }

    /**
     * Construct a Batch from a value representing an exact amount of items
     * @param value the exact amount of items, which must be greater than 0
     * @return the Batch
     */
    public static Batch asAmount(int value) {
        return Batch.custom().withBatchAsAmount(value).build();
    }

    /**
     * Returns a BatchBuilder for instantiating a custom Batch.
     *
     * @return a BatchBuilder instance.
     */
    public static BatchBuilder custom() {
        return new BatchBuilder();
    }

    /**
     * Helper class for building a Batch.
     */
    public static class BatchBuilder {
        private String batch;
        private Optional<Double> delay;
        private Optional<Integer> presencePingTimeout;

        /**
         * Constructor for BatchBuilder.
         *
         * @return a BatchBuilder instance.
         */
        private BatchBuilder() {
            this.delay = Optional.empty();
            this.presencePingTimeout = Optional.empty();
        }

        /**
         * Sets the batch value representing an exact amount of items
         * @param value the exact amount of items, which must be greater than 0
         * @return the Batch
         */
        public BatchBuilder withBatchAsAmount(int value) {
            if (value <= 0) {
                throw new IllegalArgumentException("Expected value greater than 0 to make a " +
                        "valid batch amount");
            }
            this.batch = Integer.toString(value);
            return this;
        }

        /**
         * Sets the batch value representing a percent
         * @param value the percent, which must be greater than 0 and less than or equal to 100
         * @return the Batch
         */
        public BatchBuilder withBatchAsPercent(int value) {
            if (value <= 0 || value > 100) {
                throw new IllegalArgumentException("Expected value greater than 0 and less " +
                        "than or equal to 100 to make valid batch as a percent.");
            }
            this.batch = Integer.toString(value);
            return this;
        }

        /**
         * Batch delay specifies the time for salt to wait for more minions to return a result before
         * scheduling the next batch. This helps to avoid single minion batches.
         *
         * @param delayIn time to wait in seconds.
         * @return this BatchBuilder instance with the specified batch delay.
         */
        public BatchBuilder withDelay(Double delayIn) {
            this.delay = Optional.of(delayIn);
            return this;
        }

        /**
         * Batch presence ping timeout specifies the timeout in seconds of the presence ping performed in
         * salt minions to determine which minions are available during salt batch calls.
         *
         * @param presencePingTimeoutIn time to wait in seconds.
         * @return this BatchBuilder instance with the specified batch presence ping timeout.
         */
        public BatchBuilder withPresencePingTimeout(Integer presencePingTimeoutIn) {
            this.presencePingTimeout = Optional.of(presencePingTimeoutIn);
            return this;
        }

        /**
         * Returns a Batch with the values of this BatchBuilder instance.
         *
         * @return a new Batch
         */
        public Batch build() {
            return new Batch(this);
        }

    }
}
