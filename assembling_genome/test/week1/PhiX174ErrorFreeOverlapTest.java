package week1;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for week1.PhiX174ErrorFreeOverlap.
 */
public class PhiX174ErrorFreeOverlapTest {
    private static final char[] NUKLEOTIDES = new char[]{'A', 'C', 'G', 'T'};
    private static final Random rnd = new Random();
    private PhiX174ErrorFreeOverlap solver;

    public PhiX174ErrorFreeOverlapTest() {
        solver = new PhiX174ErrorFreeOverlap(2);
    }

    private static void verifyPath(Integer[] expected, PhiX174ErrorFreeOverlap.Vertex[] path) {
        for (int i = 0; i < expected.length; i++) {
            assertEquals((int) expected[i], path[i].index);
        }
    }

    @Test
    public void testStringsOverlap_withoutOverlap() {
        // No overlapping symbols
        assertEquals(0, solver.stringsOverlap("AAAA", "BBB"));

        // Overlaps in a middle
        assertEquals(0, solver.stringsOverlap("GTACGG", "TACGAT"));
    }

    @Test
    public void testStringsOverlap_withOverlap() {
        assertEquals(2, solver.stringsOverlap("TTGC", "GCAA")); // overlap - ABA
    }

    @Test
    public void testBuildOverlapGraph_singleEdgeForward() {
        /*
         * TTGC --2--> GCAA
         */
        String[] reads = new String[]{"TTGC", "GCAA"};
        List<PhiX174ErrorFreeOverlap.Vertex>[] graph = solver.buildOverlapGraph(reads);

        assertEquals(1, graph[0].get(0).index);
        assertEquals(2, graph[0].get(0).weight);
        assertTrue(graph[1].isEmpty());
    }

    @Test
    public void testBuildOverlapGraph_singleEdgeBackward() {
        /*
         * AAAA <--2-- GCAA
         */
        String[] reads = new String[]{"AAAA", "GCAA"};
        List<PhiX174ErrorFreeOverlap.Vertex>[] graph = solver.buildOverlapGraph(reads);

        assertTrue(graph[0].isEmpty());
        assertEquals(0, graph[1].get(0).index);
        assertEquals(2, graph[1].get(0).weight);
    }

    @Test
    public void testBuildOverlapGraph_cycle() {
        /*
         *        <--4--
         * TACGTA       CTGACG
         *        --4-->
         */
        String[] reads = new String[]{"TACGTA", "CGTACG"};
        List<PhiX174ErrorFreeOverlap.Vertex>[] graph = solver.buildOverlapGraph(reads);

        assertEquals(1, graph[0].get(0).index);
        assertEquals(4, graph[0].get(0).weight);
        assertEquals(0, graph[1].get(0).index);
        assertEquals(4, graph[1].get(0).weight);
    }

    @Test
    public void testBuildOverlapGraph_vertexSortedByWeight() {
        /*
         *         --5-->GTACGT
         *        /
         * CGTACG ---4-->TACGAT
         *        \
         *         --3-->ACGTTT
         */
        String[] reads = new String[]{"CGTACG", "GTACGT", "TACGAT", "ACGTTT"};
        List<PhiX174ErrorFreeOverlap.Vertex>[] graph = solver.buildOverlapGraph(reads);

        PhiX174ErrorFreeOverlap.Vertex[] node1AdjVertices = graph[0].toArray(
                new PhiX174ErrorFreeOverlap.Vertex[graph[0].size()]);
        assertEquals(3, node1AdjVertices[2].index);
        assertEquals(3, node1AdjVertices[2].weight);

        assertEquals(2, node1AdjVertices[1].index);
        assertEquals(4, node1AdjVertices[1].weight);

        assertEquals(1, node1AdjVertices[0].index);
        assertEquals(5, node1AdjVertices[0].weight);
    }

    @Test
    public void testBuildLongestHamiltonianPath_singleEdgeVertices_noCycle() {
        /*
         * AAAC -> ACCC -> CCTA
         */
        List<PhiX174ErrorFreeOverlap.Vertex>[] graph = new List[3];
        graph[0] = new LinkedList<>();
        graph[0].add(new PhiX174ErrorFreeOverlap.Vertex(1, 1));
        graph[1] = new LinkedList<>();
        graph[1].add(new PhiX174ErrorFreeOverlap.Vertex(2, 1));
        graph[2] = new LinkedList<>();

        Deque<PhiX174ErrorFreeOverlap.Vertex> path = solver.buildLongestHamiltonianPath(graph);
        Integer[] expectedPath = {0, 1, 2};
        verifyPath(expectedPath, path.toArray(new PhiX174ErrorFreeOverlap.Vertex[path.size()]));
    }

    @Test
    public void testBuildLongestHamiltonianPath_singleEdgeVertices_withCycle() {
        /*
         * AAAC -> ACCC -> CCAA
         *   |------<-------|
         *
         */
        List<PhiX174ErrorFreeOverlap.Vertex>[] graph = new List[3];
        graph[0] = new LinkedList<>();
        graph[0].add(new PhiX174ErrorFreeOverlap.Vertex(1, 1));
        graph[1] = new LinkedList<>();
        graph[1].add(new PhiX174ErrorFreeOverlap.Vertex(2, 1));
        graph[2] = new LinkedList<>();
        graph[2].add(new PhiX174ErrorFreeOverlap.Vertex(0, 1));

        Deque<PhiX174ErrorFreeOverlap.Vertex> path = solver.buildLongestHamiltonianPath(graph);
        Integer[] expectedPath = {0, 1, 2};
        verifyPath(expectedPath, path.toArray(new PhiX174ErrorFreeOverlap.Vertex[path.size()]));
    }

    @Test
    public void testBuildLongestHamiltonianPath_multiEdgeVertices() {
        /*
         *          CGCA
         *        /      \
         *       1        1
         *      /          \
         * AAAC -----2----> ACGA
         *  \-----<--1-------/
         */
        List<PhiX174ErrorFreeOverlap.Vertex>[] graph = new List[3];
        graph[0] = new LinkedList<>();
        graph[0].add(new PhiX174ErrorFreeOverlap.Vertex(1, 1));
        graph[0].add(new PhiX174ErrorFreeOverlap.Vertex(2, 2));
        graph[1] = new LinkedList<>();
        graph[1].add(new PhiX174ErrorFreeOverlap.Vertex(2, 1));
        graph[2] = new LinkedList<>();
        graph[2].add(new PhiX174ErrorFreeOverlap.Vertex(0, 1));

        Deque<PhiX174ErrorFreeOverlap.Vertex> path = solver.buildLongestHamiltonianPath(graph);
        Integer[] expectedPath = {0, 1, 2};
        verifyPath(expectedPath, path.toArray(new PhiX174ErrorFreeOverlap.Vertex[path.size()]));
    }

    @Test
    public void testAssemblyGenomeFromReadsAndPath() {
        String[] reads = new String[]{"AAAC", "CGCA", "ACGA"};
        Deque<PhiX174ErrorFreeOverlap.Vertex> path = new LinkedList<>();
        path.add(new PhiX174ErrorFreeOverlap.Vertex(0, 0));
        path.add(new PhiX174ErrorFreeOverlap.Vertex(1, 1));
        path.add(new PhiX174ErrorFreeOverlap.Vertex(2, 1));
        String genome = solver.assemblyGenome(reads, path);

        assertEquals("AAACGCACG", genome);
    }

    @Test
    public void testAssemblyGenomeFromReads_simple() {
        String[] reads = new String[]{"AACG", "CGCA", "CAAA"};
        String genome = solver.assemblyGenome(reads);

        verifyGenome("AACGCA", genome);
    }

    @Test
    public void testAssemblyGenomeFromReads_example1() {
        solver = new PhiX174ErrorFreeOverlap(5);
        String[] reads = new String[]{"GTACGT", "TACGTA", "CGTACG", "ACGTAC", "GTACGA", "TACGAT",
                "ACGATG", "CGATGT", "GATGTA", "ATGTAC", "TGTACG"};
        String genome = solver.assemblyGenome(reads);

        verifyGenome("GTACGTACGAT", genome);
    }

    @Test
    public void testAssemblyGenomeFromReads_example2() {
        solver = new PhiX174ErrorFreeOverlap(1);
        String[] reads = new String[]{"TAG", "AGT", "CAG", "GCA"};
        String genome = solver.assemblyGenome(reads);

        verifyGenome("TAGCAG", genome);
    }

    @Test
    public void testAssemblyGenomeFromReads_20fixed() {
        String[] reads = new String[]{
                "GACAA", "AGGGA", "ACAAC", "GAAGG", "ATTGA", "TGTTG", "ATTGA", "CTGTT", "ACAAC",
                "CTGTT", "GTTGG", "ACTGT", "GATTG", "ACAAC", "AGGGA", "GGATT", "TTGAA", "AGGGA",
                "TTGAA", "TGGAT", "ACTGT", "AAGGG", "GGATT", "TGAAG", "GACAA", "CAACT", "AACTG",
                "TGTTG", "TTGAA", "GAAGG"};

        solver = new PhiX174ErrorFreeOverlap(3);
        String assemblyGenome = solver.assemblyGenome(reads);

        System.out.println("Assembled genome: " + assemblyGenome);

        verifyGenome("TGGATTGAAGGGACAACTGT", assemblyGenome);
    }

    @Test
    public void testAssemblyGenomeFromReads_10fixed() {
        String[] reads = new String[]{
                "ATAAG", "TTCAT", "GGTTC", "GGGTT", "ATAAG", "CATAA", "TTCAT", "GGGTT", "GTTCA",
                "ATAAG", "ATAAG", "GTTCA", "AAGGG", "AAGGG", "ATAAG", "TTCAT", "TTCAT", "GGTTC", "ATAAG",
                "GTTCA"};

        solver = new PhiX174ErrorFreeOverlap(2);
        String assemblyGenome = solver.assemblyGenome(reads);

        System.out.println("Assembled genome: " + assemblyGenome);

        verifyGenome("AAGGGTTCAT", assemblyGenome);
    }

    @Test
    public void testAssemblyGenomeFromReads_10() {
        char[] genomeChars = new char[10];
        for (int i = 0; i < genomeChars.length; i++) {
            genomeChars[i] = NUKLEOTIDES[rnd.nextInt(NUKLEOTIDES.length)];
        }

        System.out.println("Source: " + String.valueOf(genomeChars));
        String[] reads = new String[20];
        System.out.println("Reads:");
        for (int i = 0; i < reads.length; i++) {
            reads[i] = generateRead(genomeChars, 5);
            System.out.println(reads[i]);
        }
        System.out.println("Reads Build!");

        solver = new PhiX174ErrorFreeOverlap(3);
        String assemblyGenome = solver.assemblyGenome(reads);

        System.out.println("Assembled genome: " + assemblyGenome);

        String genomeString = String.valueOf(genomeChars);
        verifyGenome(genomeString, assemblyGenome);
    }

    @Test
    public void testAssemblyGenomeFromReads_20() {
        char[] genomeChars = new char[20];
        for (int i = 0; i < genomeChars.length; i++) {
            genomeChars[i] = NUKLEOTIDES[rnd.nextInt(NUKLEOTIDES.length)];
        }

        System.out.println("Source: " + String.valueOf(genomeChars));
        String[] reads = new String[30];
        System.out.println("Reads:");
        for (int i = 0; i < reads.length; i++) {
            reads[i] = generateRead(genomeChars, 5);
            System.out.println(reads[i]);
        }
        System.out.println("Reads Build!");

        solver = new PhiX174ErrorFreeOverlap(3);
        String assemblyGenome = solver.assemblyGenome(reads);

        System.out.println("Assembled genome: " + assemblyGenome);

        String genomeString = String.valueOf(genomeChars);
        verifyGenome(genomeString, assemblyGenome);
    }

    private void verifyGenome(String expected, String actual) {
        assertTrue(expected.length() == actual.length());

        int forwardOverlap = solver.stringsOverlap(expected, actual);
        int backwardOverlap = solver.stringsOverlap(actual, expected);

        assertTrue(forwardOverlap + backwardOverlap == expected.length());
    }

    private static String generateRead(char[] genome, int readLength) {
        char[] readChars = new char[readLength];
        int startPosition = rnd.nextInt(genome.length);

        char[] subRead = Arrays.copyOfRange(genome, startPosition, Math.min(startPosition +
                readLength, genome.length));
        System.arraycopy(subRead, 0, readChars, 0, subRead.length);

        if (subRead.length < readLength) {
            System.arraycopy(genome, 0, readChars, subRead.length, readLength - subRead.length);
        }

        return String.valueOf(readChars);
    }
}