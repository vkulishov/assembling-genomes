package week3;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link SelectOptimalKmer}.
 */
public class SelectOptimalKmerTest {
    private SelectOptimalKmer solver = new SelectOptimalKmer();

    private static Set<String> buildReads(String... reads) {
        return new HashSet<>(Arrays.asList(reads));
    }

    @Test
    public void findOptimalK_sample1() throws Exception {
        Set<String> reads = buildReads("AACG",
                "ACGT",
                "CAAC",
                "GTTG",
                "TGCA");

        int optimalK = solver.findOptimalK(reads);
        assertEquals(3, optimalK);
    }
}