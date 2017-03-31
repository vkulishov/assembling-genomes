package week3;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static util.CommonTestHelper.buildReads;

/**
 * Unit tests for {@link BubbleDetection}.
 */
public class BubbleDetectionTest {
    private BubbleDetection solver = new BubbleDetection();

    @Test
    public void testCountBubbles_sample1() throws Exception {
        int bubbles = solver.countBubbles(3, 3, buildReads(
                "AACG",
                "AAGG",
                "ACGT",
                "AGGT",
                "CGTT",
                "GCAA",
                "GGTT",
                "GTTG",
                "TGCA",
                "TTGC"
        ));

        assertEquals(1, bubbles);
    }

    @Test
    public void testCountBubbles_sample2() throws Exception {
        int bubbles = solver.countBubbles(3, 3, buildReads(
                "AABBA",
                "AACBA",
                "AADBA"
        ));

        assertEquals(3, bubbles);
    }

    @Test
    public void testCountBubbles_sample3() throws Exception {
        int bubbles = solver.countBubbles(4, 6, buildReads(
                "ATCCTAG",
                "TCCTAGA",
                "ATCGTCA",
                "CGTCAGA",
                "CGTTTCA",
                "TTTCAGA"
        ));

        assertEquals(2, bubbles);
    }

    @Test
    public void testCountBubbles_sample4() throws Exception {
        int bubbles = solver.countBubbles(3, 4, buildReads(
                "ATGCAG",
                "ATCGCA",
                "ATACGC"
        ));

        assertEquals(3, bubbles);
    }

}