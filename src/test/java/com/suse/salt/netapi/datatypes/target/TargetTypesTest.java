package com.suse.salt.netapi.datatypes.target;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;

/**
 * Tests for all target types
 */
public class TargetTypesTest {

    private final String key = "key";
    private final String value = "value";
    private final char alt_delim = '#';
    private final String def_expr = key + DictionaryTarget.DEFAULT_DELIMITER + value;
    private final String alt_expr = key + alt_delim + value;

    @Test
    public void testGrains1() {
        Grains target = new Grains(key, value);
        assertEquals(TargetType.GRAIN, target.getType());
        defaultDelimiterTestHelper(target);
    }

    @Test
    public void testGrains2() {
        Grains target = new Grains(key, value, alt_delim);
        assertEquals(TargetType.GRAIN, target.getType());
        alternateDelimiterTestHelper(target);
    }

    @Test
    public void testGrains3() {
        Grains target = new Grains(def_expr);
        assertEquals(TargetType.GRAIN, target.getType());
        defaultDelimiterTestHelper(target);
    }

    @Test
    public void testGrains4() {
        Grains target = new Grains(alt_expr, alt_delim);
        assertEquals(TargetType.GRAIN, target.getType());
        alternateDelimiterTestHelper(target);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testGrainsDeprecated() {
        Grains target = new Grains(key, value);
        assertEquals(key, target.getGrain());
    }

    @Test
    public void testGrainsRegEx1() {
        GrainsRegEx target = new GrainsRegEx(key, value);
        assertEquals(TargetType.GRAIN_PCRE, target.getType());
        defaultDelimiterTestHelper(target);
    }

    @Test
    public void testGrainsRegEx2() {
        GrainsRegEx target = new GrainsRegEx(key, value, alt_delim);
        assertEquals(TargetType.GRAIN_PCRE, target.getType());
        alternateDelimiterTestHelper(target);
    }

    @Test
    public void testGrainsRegEx3() {
        GrainsRegEx target = new GrainsRegEx(def_expr);
        assertEquals(TargetType.GRAIN_PCRE, target.getType());
        defaultDelimiterTestHelper(target);
    }

    @Test
    public void testGrainsRegEx4() {
        GrainsRegEx target = new GrainsRegEx(alt_expr, alt_delim);
        assertEquals(TargetType.GRAIN_PCRE, target.getType());
        alternateDelimiterTestHelper(target);
    }

    @Test
    public void testPillar1() {
        Pillar target = new Pillar(key, value);
        assertEquals(TargetType.PILLAR, target.getType());
        defaultDelimiterTestHelper(target);
    }

    @Test
    public void testPillar2() {
        Pillar target = new Pillar(key, value, alt_delim);
        assertEquals(TargetType.PILLAR, target.getType());
        alternateDelimiterTestHelper(target);
    }

    @Test
    public void testPillar3() {
        Pillar target = new Pillar(def_expr);
        assertEquals(TargetType.PILLAR, target.getType());
        defaultDelimiterTestHelper(target);
    }

    @Test
    public void testPillar4() {
        Pillar target = new Pillar(alt_expr, alt_delim);
        assertEquals(TargetType.PILLAR, target.getType());
        alternateDelimiterTestHelper(target);
    }

    @Test
    public void testPillarExact1() {
        PillarExact target = new PillarExact(def_expr);
        assertEquals(TargetType.PILLAR_EXACT, target.getType());
        defaultDelimiterTestHelper(target);
    }

    @Test
    public void testPillarExact2() {
        PillarExact target = new PillarExact(alt_expr, alt_delim);
        assertEquals(TargetType.PILLAR_EXACT, target.getType());
        alternateDelimiterTestHelper(target);
    }

    @Test
    public void testPillarExact3() {
        PillarExact target = new PillarExact(key, value);
        assertEquals(TargetType.PILLAR_EXACT, target.getType());
        defaultDelimiterTestHelper(target);
    }

    @Test
    public void testPillarExact4() {
        PillarExact target = new PillarExact(key, value, alt_delim);
        assertEquals(TargetType.PILLAR_EXACT, target.getType());
        alternateDelimiterTestHelper(target);
    }

    @Test
    public void testPillarRegEx1() {
        PillarRegEx target = new PillarRegEx(key, value);
        assertEquals(TargetType.PILLAR_PCRE, target.getType());
        defaultDelimiterTestHelper(target);
    }

    @Test
    public void testPillarRegEx2() {
        PillarRegEx target = new PillarRegEx(key, value, alt_delim);
        assertEquals(TargetType.PILLAR_PCRE, target.getType());
        alternateDelimiterTestHelper(target);
    }

    @Test
    public void testPillarRegEx3() {
        PillarRegEx target = new PillarRegEx(def_expr);
        assertEquals(TargetType.PILLAR_PCRE, target.getType());
        defaultDelimiterTestHelper(target);
    }

    @Test
    public void testPillarRegEx4() {
        PillarRegEx target = new PillarRegEx(alt_expr, alt_delim);
        assertEquals(TargetType.PILLAR_PCRE, target.getType());
        alternateDelimiterTestHelper(target);
    }

    @Test
    public void testCompound() {
        Compound target = new Compound("*");
        assertEquals(TargetType.COMPOUND, target.getType());
    }

    @Test
    public void testGlob1() {
        Glob target = Glob.ALL;
        assertEquals(TargetType.GLOB, target.getType());
    }

    @Test
    public void testGlob2() {
        Glob target = new Glob();
        assertEquals(TargetType.GLOB, target.getType());
    }

    @Test
    public void testIPCidr() {
        IPCidr target = new IPCidr("0.0.0.0/0");
        assertEquals(TargetType.IPCIDR, target.getType());
    }

    @Test
    public void testNodeGroup() {
        NodeGroup target = new NodeGroup("group");
        assertEquals(TargetType.NODEGROUP, target.getType());
    }

    @Test
    public void testRange() {
        Range target = new Range("range");
        assertEquals(TargetType.RANGE, target.getType());
    }

    @Test
    public void testRegEx() {
        RegEx target = new RegEx(".*");
        assertEquals(TargetType.PCRE, target.getType());
    }

    @Test
    public void testMinionList1() {
        List<String> minions = Arrays.asList("min1", "min2");
        MinionList target = new MinionList(minions);
        assertEquals(TargetType.LIST, target.getType());
    }

    @Test
    public void testMinionList2() {
        MinionList target = new MinionList("min1", "min2");
        assertEquals(TargetType.LIST, target.getType());
    }

    @Test(expected = InvalidParameterException.class)
    public void testInvalidExpression1() {
        new Grains(":value");
    }

    @Test(expected = InvalidParameterException.class)
    public void testInvalidExpression2() {
        new Grains("value");
    }

    @Test(expected = InvalidParameterException.class)
    public void testInvalidExpression3() {
        new Grains("value:");
    }

    private void defaultDelimiterTestHelper(DictionaryTarget target) {
        assertEquals(def_expr, target.getTarget());
        assertEquals(key, target.getKey());
        assertEquals(value, target.getValue());
        assertTrue(target.getProps().containsKey("tgt"));
        assertTrue(target.getProps().containsKey("tgt_type"));
        assertFalse(target.getProps().containsKey("delimiter"));
        assertEquals(DictionaryTarget.DEFAULT_DELIMITER, target.getDelimiter());
    }

    private void alternateDelimiterTestHelper(DictionaryTarget target) {
        assertEquals(alt_expr, target.getTarget());
        assertEquals(key, target.getKey());
        assertEquals(value, target.getValue());
        assertTrue(target.getProps().containsKey("tgt"));
        assertTrue(target.getProps().containsKey("tgt_type"));
        assertTrue(target.getProps().containsKey("delimiter"));
        assertEquals(alt_delim, target.getDelimiter());
    }
}
