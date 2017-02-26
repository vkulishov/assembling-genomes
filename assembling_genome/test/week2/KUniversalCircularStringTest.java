package week2;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link KUniversalCircularString}.
 */
public class KUniversalCircularStringTest {
    private KUniversalCircularString solver = new KUniversalCircularString();

    @Test
    public void buildKMers() throws Exception {
        List<String> kMers = solver.buildKMers(3);
        List<String> expectedKmers = Arrays.asList("000", "001", "100", "010", "011", "101", "110", "111");
        assertTrue(kMers.size() == expectedKmers.size());
        assertTrue(kMers.containsAll(expectedKmers));
    }

    @Test
    public void testDeBruijnGraphBuild_singleEdge() {
        KUniversalCircularString.DeBruijnGraph graph = new KUniversalCircularString.DeBruijnGraph();
        graph.addKMer("001");

        assertTrue(graph.getVertices().size() == 2);

        KUniversalCircularString.Vertex sourceVertex = graph.getVertexMap().get("00");
        KUniversalCircularString.Vertex destinationVertex = graph.getVertexMap().get("01");

        assertEquals("00", sourceVertex.getId());
        assertEquals("01", destinationVertex.getId());
        assertTrue(sourceVertex.getOutgoingEdges().equals(destinationVertex.getInboundEdges()));
    }

    @Test
    public void testDeBruijnGraphBuild_selfLoopEdge() {
        KUniversalCircularString.DeBruijnGraph graph = new KUniversalCircularString.DeBruijnGraph();
        graph.addKMer("000");

        assertTrue(graph.getVertices().size() == 1);

        KUniversalCircularString.Vertex vertex = graph.getVertexMap().get("00");
        assertEquals("00", vertex.getId());
        assertTrue(vertex.getOutgoingEdges().get(0).isSelfLoop());
    }

    @Test
    public void testDeBruijnGraphBuild_2NonOverlappingKmers() {
        KUniversalCircularString.DeBruijnGraph graph = new KUniversalCircularString.DeBruijnGraph();
        graph.addKMer("000");
        graph.addKMer("111");

        assertTrue(graph.getVertices().size() == 2);

        KUniversalCircularString.Vertex vertex1 = graph.getVertexMap().get("00");
        KUniversalCircularString.Vertex vertex2 = graph.getVertexMap().get("11");

        assertEquals("00", vertex1.getId());
        assertEquals("11", vertex2.getId());
        assertTrue(vertex1.getOutgoingEdges().get(0).isSelfLoop());
        assertTrue(vertex2.getOutgoingEdges().get(0).isSelfLoop());
    }

    @Test
    public void testDeBruijnGraphBuild_2OverlappingKmers() {
        KUniversalCircularString.DeBruijnGraph graph = new KUniversalCircularString.DeBruijnGraph();
        graph.addKMer("001");
        graph.addKMer("010");

        assertTrue(graph.getVertices().size() == 3);

        KUniversalCircularString.Vertex vertex00 = graph.getVertexMap().get("00");
        KUniversalCircularString.Vertex vertex01 = graph.getVertexMap().get("01");
        KUniversalCircularString.Vertex vertex10 = graph.getVertexMap().get("10");

        assertTrue(vertex00.getInboundEdges().size() == 0);
        assertTrue(vertex00.getOutgoingEdges().size() == 1);
        KUniversalCircularString.Edge edge001 = vertex00.getOutgoingEdges().get(0);
        assertEquals("001", edge001.getName());
        assertEquals("00", edge001.getSourceVertexId());
        assertEquals("01", edge001.getDestinationVertexId());

        assertTrue(vertex01.getInboundEdges().size() == 1);
        assertTrue(vertex01.getOutgoingEdges().size() == 1);
        assertEquals(edge001, vertex01.getInboundEdges().get(0));
        KUniversalCircularString.Edge edge010 = vertex01.getOutgoingEdges().get(0);
        assertEquals("010", edge010.getName());
        assertEquals("01", edge010.getSourceVertexId());
        assertEquals("10", edge010.getDestinationVertexId());

        assertTrue(vertex10.getInboundEdges().size() == 1);
        assertTrue(vertex10.getOutgoingEdges().size() == 0);
        assertEquals(edge010, vertex10.getInboundEdges().get(0));
    }

    @Test
    public void testDeBruijnGraphBuild_2EqualKmers() {
        KUniversalCircularString.DeBruijnGraph graph = new KUniversalCircularString.DeBruijnGraph();
        graph.addKMer("010");
        graph.addKMer("010");

        assertTrue(graph.getVertices().size() == 2);

        KUniversalCircularString.Vertex vertex01 = graph.getVertexMap().get("01");
        KUniversalCircularString.Vertex vertex10 = graph.getVertexMap().get("10");

        assertEquals("01", vertex01.getId());
        assertEquals("10", vertex10.getId());

        assertTrue(vertex01.getOutgoingEdges().size() == 2);
        assertTrue(vertex10.getInboundEdges().size() == 2);
    }

    @Test
    public void testFindKUniversalString3() {
        String kuniversalString = solver.findKuniversalString(3);
        List<String> kmers = solver.buildKMers(3);

        for (String kmer : kmers) {
            assertEquals(1, countSubString(kuniversalString, kmer));
        }
    }

    @Test
    public void testFindKUniversalString4() {
        String kuniversalString = solver.findKuniversalString(4);
        List<String> kmers = solver.buildKMers(4);

        for (String kmer : kmers) {
            assertEquals(1, countSubString(kuniversalString, kmer));
        }
    }

    @Test
    public void testFindKUniversalStringRangeFrom4To14() {
        for (int k = 4; k < 14; k++) {
            String kuniversalString = solver.findKuniversalString(k);
            List<String> kmers = solver.buildKMers(k);

            for (String kmer : kmers) {
                assertEquals(1, countSubString(kuniversalString, kmer));
            }
        }
    }

    private static int countSubString(String universalString, String substring) {
        char[] stringChars = universalString.toCharArray();
        char[] substringChars = substring.toCharArray();

        int count = 0;
        int pointer = 0;
        while (pointer < stringChars.length - substringChars.length) {
            if (Arrays.equals(substringChars, Arrays.copyOfRange(stringChars, pointer, pointer + substringChars.length))) {
                count++;
            }
            pointer++;
        }

        while (pointer < stringChars.length) {
            char[] cycleStringChars = new char[substring.length()];
            System.arraycopy(stringChars, pointer, cycleStringChars, 0, stringChars.length - pointer);
            System.arraycopy(stringChars, 0, cycleStringChars, stringChars.length - pointer, substring.length() - (stringChars.length - pointer));

            if (Arrays.equals(substringChars, cycleStringChars)) {
                count++;
            }
            pointer++;
        }
        return count;
    }
}