package util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Custom asserts used in the projects' unit tests.
 */
public class Asserts {

    /**
     * Compare provided circular genomes.
     */
    public static void assertGenomeEquals(String expected, String actual) {
        if (expected.equals(actual)) {
            return;
        }

        System.out.println("Expected:" + expected + "\nactual:" + actual);
        assertTrue(expected.length() == actual.length());
        String doubleExpected = expected + expected;
        assertTrue(doubleExpected.contains(actual));
    }

}
