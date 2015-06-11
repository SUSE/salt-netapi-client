package com.suse.saltstack.netapi.parser;

import java.util.Optional;
import java.util.List;

/**
 * Helper Type to test the Optional Parser
 */
public class OptionalTest {

    public Optional<String> nullString = Optional.empty();
    public Optional<String> valueString = Optional.empty();
    public Optional<String> absentString = Optional.empty();
    public List<Optional<Integer>> maybeInts;

}
