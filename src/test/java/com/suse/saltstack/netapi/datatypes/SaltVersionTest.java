package com.suse.saltstack.netapi.datatypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Optional;

/**
 * SaltVersion class unit-tests.
 */
public class SaltVersionTest {

    @Test
    public void testConstructor() {
        SaltVersion v1  = new SaltVersion(2015, 8, 0);

        assertEquals(2015, v1.getYear());
        assertEquals(8, v1.getMonth());
        assertEquals(0, v1.getBugfix());
        assertFalse(v1.getReleaseCandidate().isPresent());

        SaltVersion v1rc2 = new SaltVersion(2015, 8, 0, 2);

        assertEquals(2015, v1rc2.getYear());
        assertEquals(8, v1rc2.getMonth());
        assertEquals(0, v1rc2.getBugfix());
        assertTrue(v1rc2.getReleaseCandidate().map(rc -> rc == 2).orElse(false));
    }

    @Test
    public void testParsing() {
        SaltVersion v1rc2 = SaltVersion.parse("2015.8.0rc2").get();
        assertEquals(2015, v1rc2.getYear());
        assertEquals(8, v1rc2.getMonth());
        assertEquals(0, v1rc2.getBugfix());
        assertTrue(v1rc2.getReleaseCandidate().map(rc -> rc == 2).orElse(false));

        SaltVersion v2 = SaltVersion.parse("2013.4.0").get();
        assertEquals(2013, v2.getYear());
        assertEquals(4, v2.getMonth());
        assertEquals(0, v2.getBugfix());
        assertFalse(v2.getReleaseCandidate().isPresent());

        Optional<SaltVersion> v3 = SaltVersion.parse("2015.8.0r2");
        assertFalse(v3.isPresent());
    }

    @Test
    public void testComparisonRC() {
        SaltVersion v1  = new SaltVersion(2015, 8, 0);
        SaltVersion v1rc1 = new SaltVersion(2015, 8, 0, 1);
        SaltVersion v1rc2 = new SaltVersion(2015, 8, 0, 2);
        assertTrue("A version with rc should be smaller then the same version without rc",
                v1.compareTo(v1rc1) > 0 && v1rc1.compareTo(v1) < 0);
        assertTrue("smaller rc number on the same version should make the version smaller",
                v1rc1.compareTo(v1rc2) < 0 && v1rc2.compareTo(v1rc1) > 0);
    }

    @Test
    public void testComparisonYear() {
        SaltVersion v1 = new SaltVersion(2016, 8, 0);
        SaltVersion v2  = new SaltVersion(2015, 8, 0);
        assertTrue("a smaller year should result in a smaller version",
                v1.compareTo(v2) > 0 && v2.compareTo(v1) < 0);
    }

    @Test
    public void testComparisonMonth() {
        SaltVersion v1 = new SaltVersion(2015, 8, 0);
        SaltVersion v2  = new SaltVersion(2015, 6, 0);
        assertTrue("a smaller month should result in a smaller version",
                v1.compareTo(v2) > 0 && v2.compareTo(v1) < 0);
    }

    @Test
    public void testComparisonBugfix() {
        SaltVersion v1 = new SaltVersion(2015, 8, 3);
        SaltVersion v2  = new SaltVersion(2015, 8, 1);
        assertTrue("a smaller bugfix should result in a smaller version",
                v1.compareTo(v2) > 0 && v2.compareTo(v1) < 0);
    }

    @Test
    public void testToString() {
        SaltVersion v1 = new SaltVersion(2015, 8, 3);
        SaltVersion v1rc2 = new SaltVersion(2015, 8, 0, 2);
        assertEquals("2015.8.3", v1.toString());
        assertEquals("2015.8.0rc2", v1rc2.toString());
    }
}
