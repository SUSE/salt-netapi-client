package com.suse.salt.netapi.datatypes;

import org.junit.Test;

/**
 * Test the verification of numeric batch re
 */
public class BatchTest {

    @Test(expected = IllegalArgumentException.class)
    public void percentGreaterThan100ShouldThrowException() {
        Batch.asPercent(101);
    }

    @Test(expected = IllegalArgumentException.class)
    public void percentEqualToZeroShouldThrowException() {
        Batch.asPercent(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void amountEqualToZeroShouldThrowException() {
        Batch.asAmount(0);
    }
}
