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
    private Optional<Integer> gatherJobTimeout;
    private Optional<Integer> timeout;

    private Batch(BatchBuilder builder) {
        this.batch = builder.batch;
        this.delay = builder.delay;
        this.gatherJobTimeout = builder.gatherJobTimeout;
        this.timeout = builder.timeout;
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

    public Optional<Integer> getGatherJobTimeout() {
        return gatherJobTimeout;
    }

    public Map<String, Object> getParams() {
        Map<String, Object> customArgs = new HashMap<>();
        customArgs.put("batch", batch);
        delay.ifPresent(d -> {
            customArgs.put("batch_delay", d);
        });
        gatherJobTimeout.ifPresent(d -> {
            customArgs.put("gather_job_timeout", d);
        });
        timeout.ifPresent(d -> {
            customArgs.put("timeout", d);
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
        private Optional<Integer> gatherJobTimeout;
        private Optional<Integer> timeout;

        /**
         * Constructor for BatchBuilder.
         *
         * @return a BatchBuilder instance.
         */
        private BatchBuilder() {
            this.delay = Optional.empty();
            this.gatherJobTimeout = Optional.empty();
            this.timeout = Optional.empty();
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
         * Salt will execute an implicit ping to all the targets of this call before the call itself
         * in order to compute the list of minions that will actually be batched.
         *
         * If any minions haven't responded within {@link Batch#timeout} seconds, Salt will call `find_job` to
         * determine if a job is at least running on them.
         *
         * This parameter specifies the maximum number of seconds `find_job` will wait before assuming the minion
         * is not responsive, thus skipping it.
         *
         * The total amount of time before any minion is skipped is thus:
         * {@link Batch#timeout} + {@link Batch#gatherJobTimeout}
         *
         * @param gatherJobTimeoutIn time to wait in seconds.
         * @return this BatchBuilder instance with the gather job timeout.
         */
        public BatchBuilder withGatherJobTimeout(Integer gatherJobTimeoutIn) {
            this.gatherJobTimeout = Optional.of(gatherJobTimeoutIn);
            return this;
        }

        /**
         * Salt will execute an implicit ping to all the targets of this call before the call itself
         * in order to compute the list of minions that will actually be batched.
         *
         * If any minions haven't responded within this amount of seconds, Salt will call `find_job` to
         * determine if a job is at least running on them.
         *
         * The total amount of time before any minion is skipped is thus:
         * {@link Batch#timeout} + {@link Batch#gatherJobTimeout}
         *
         * @param timeoutIn time to wait in seconds.
         * @return this BatchBuilder instance with the specified batch presence ping timeout.
         */
        public BatchBuilder withTimeout(Integer timeoutIn) {
            this.timeout = Optional.of(timeoutIn);
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
