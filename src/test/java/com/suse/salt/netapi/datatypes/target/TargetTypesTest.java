package com.suse.salt.netapi.datatypes.target;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Tests for all target types
 */
public class TargetTypesTest {

    private final String key = "key";
    private final String value = "value";
    private final char delimiter = DictionaryTargetExpression.DEFAULT_DELIMITER;
    private final String expr = key + delimiter + value;

    @Test
    public void testGrains() {
        Grains target = new Grains(new DictionaryTargetExpression(expr));
        assertEquals(TargetType.GRAIN, target.getType());
        dictionaryTargetTestHelper(target);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testGrainsDeprecated() {
        Grains target = new Grains(key, value);
        assertEquals(key, target.getGrain());
    }

    @Test
    public void testGrainsRegEx() {
        GrainsRegEx target = new GrainsRegEx(new DictionaryTargetExpression(expr));
        assertEquals(TargetType.GRAIN_PCRE, target.getType());
        dictionaryTargetTestHelper(target);
    }

    @Test
    public void testPillar() {
        Pillar target = new Pillar(new DictionaryTargetExpression(expr));
        assertEquals(TargetType.PILLAR, target.getType());
        dictionaryTargetTestHelper(target);
    }

    @Test
    public void testPillarExact() {
        PillarExact target = new PillarExact(new DictionaryTargetExpression(expr));
        assertEquals(TargetType.PILLAR_EXACT, target.getType());
        dictionaryTargetTestHelper(target);
    }

    @Test
    public void testPillarRegEx() {
        PillarRegEx target = new PillarRegEx(new DictionaryTargetExpression(expr));
        assertEquals(TargetType.PILLAR_PCRE, target.getType());
    }

    @Test
    public void testAlternateDelimiter() {
        DictionaryTargetExpression expr = new DictionaryTargetExpression(key, value, '#');
        Pillar target = new Pillar(expr);
        assertTrue(target.getProps().containsKey("tgt"));
        assertTrue(target.getProps().containsKey("expr_form"));
        assertTrue(target.getProps().containsKey("delimiter"));
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

    private void dictionaryTargetTestHelper(DictionaryTarget target) {
        assertEquals(expr, target.getTarget());
        assertEquals(key, target.getKey());
        assertEquals(value, target.getValue());
        assertEquals(delimiter, target.getDelimiter());
        assertTrue(target.getProps().containsKey("tgt"));
        assertTrue(target.getProps().containsKey("expr_form"));
        assertFalse(target.getProps().containsKey("delimiter"));
    }
}
