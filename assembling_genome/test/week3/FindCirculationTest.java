package week3;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link FindCirculation} methods and sublasses.
 */
public class FindCirculationTest {

    private FindCirculation solver = new FindCirculation();

    private static FindCirculation.FlowNetworkGraph buildGraph(int n, String[] edges) {
        FindCirculation.FlowNetworkGraph graph = new FindCirculation.FlowNetworkGraph(n, edges.length);

        for (String edge : edges) {
            String[] edgeParts = edge.split(" ");
            int from = Integer.parseInt(edgeParts[0]) - 1;
            int to = Integer.parseInt(edgeParts[1]) - 1;
            int lowerBuond = Integer.parseInt(edgeParts[2]);
            int capacity = Integer.parseInt(edgeParts[3]);
            graph.addEdge(from, to, lowerBuond, capacity);
        }

        return graph;
    }

    @Test
    public void testGraphBuild() {
        FindCirculation.FlowNetworkGraph graph = new FindCirculation.FlowNetworkGraph(2, 3);
        graph.addEdge(0, 1, 1, 2);
        graph.addEdge(0, 1, 1, 2);
        graph.addEdge(1, 0, 2, 2);

        assertTrue(graph.getVertices().size() == 2);

        FindCirculation.Vertex vertex0 = graph.getVertices().get(0);
        FindCirculation.Vertex vertex1 = graph.getVertices().get(1);

        assertFalse(vertex0.getInboundEdges().isEmpty());
        assertFalse(vertex0.getOutgoingEdges().isEmpty());

        assertEquals(vertex0.getOutgoingEdges(), vertex1.getInboundEdges());
        assertEquals(vertex0.getInboundEdges(), vertex1.getOutgoingEdges());

        assertEquals(6, graph.getEdges().size());
    }

    @Test
    public void testFindFlowPath_simplePath() {
        FindCirculation.FlowNetworkGraph graph = buildGraph(3, new String[]{"1 2 0 3", "2 3 0 3"});
        FindCirculation.Vertex source = graph.getVertices().get(0);
        FindCirculation.Vertex target = graph.getVertices().get(2);
        FindCirculation.FlowPath path = solver.findFlowPath(graph, source, target);

        assertEquals(3, path.getFlow());
    }

    @Test
    public void testFindFlowPath_simplePath_differentCapacities() {
        FindCirculation.FlowNetworkGraph graph = buildGraph(3, new String[]{"1 2 0 3", "2 3 0 1"});
        FindCirculation.Vertex source = graph.getVertices().get(0);
        FindCirculation.Vertex target = graph.getVertices().get(2);
        FindCirculation.FlowPath path = solver.findFlowPath(graph, source, target);

        assertEquals(1, path.getFlow());
    }

    @Test
    public void testFindFlowPath_useShortestPath() {
        FindCirculation.FlowNetworkGraph graph = buildGraph(4, new String[]{"1 2 0 1000", "2 3 0 1", "1 3 0 1000", "3 2 0 1", "2 4 0 1000", "3 4 0 1000"});
        FindCirculation.Vertex source = graph.getVertices().get(0);
        FindCirculation.Vertex target = graph.getVertices().get(3);
        FindCirculation.FlowPath path = solver.findFlowPath(graph, source, target);

        assertEquals(1000, path.getFlow());
        assertEquals(source, path.getEdges().removeFirst().getFrom());
        assertEquals(target, path.getEdges().removeFirst().getTo());
    }

    @Test
    public void testReduceToNetworkFlowGraph() {
        FindCirculation.FlowNetworkGraph graph = buildGraph(2, new String[]{"1 2 1 3"});
        solver.reduceToNetworkFlowGraph(graph);

        assertEquals(4, graph.getVertices().size());

        assertEquals(1, graph.getSource().getOutgoingEdges().get(0).getTo().getId());
        assertEquals(0, graph.getTarget().getInboundEdges().get(0).getFrom().getId());

        FindCirculation.Edge edge01 = graph.getEdges().get(0);
        assertEquals(2, edge01.getCapacity());
    }

    @Test
    public void testFindCirculation_sample1() {
        FindCirculation.FlowNetworkGraph graph = buildGraph(3, new String[]{"1 2 0 3", "2 3 0 3"});

        FindCirculation.CirculationResponse circulation = solver.findCirculation(graph);
        assertEquals("YES\n0\n0\n", circulation.toString());
    }

    @Test
    public void testFindCirculation_sample2() {
        FindCirculation.FlowNetworkGraph graph = buildGraph(3, new String[]{"1 2 1 3", "2 3 2 4", "3 1 1 2"});

        FindCirculation.CirculationResponse circulation = solver.findCirculation(graph);
        assertEquals("YES\n2\n2\n2\n", circulation.toString());
    }

    @Test
    public void testFindCirculation_sample3() {
        FindCirculation.FlowNetworkGraph graph = buildGraph(3, new String[]{"1 2 1 3", "2 3 2 4", "1 3 1 2"});

        FindCirculation.CirculationResponse circulation = solver.findCirculation(graph);
        assertEquals("NO", circulation.toString());
    }

    @Test
    public void testFindCirculation_parallelEdges() {
        FindCirculation.FlowNetworkGraph graph = buildGraph(2, new String[]{"1 2 1 1", "1 2 1 1", "2 1 1 2"});

        FindCirculation.CirculationResponse circulation = solver.findCirculation(graph);
        assertEquals("YES\n1\n1\n2\n", circulation.toString());
    }
}