package week2;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests for {@link FindEulerCycle}.
 */
public class FindEulerCycleTest {
    private FindEulerCycle solver = new FindEulerCycle();

    private static FindEulerCycle.Graph buildGraph(int n, int m, int[][] edges) {
        FindEulerCycle.Vertex[] vertices = new FindEulerCycle.Vertex[n];
        for (int i = 0; i < n; i++) {
            vertices[i] = new FindEulerCycle.Vertex(i);
        }
        for (int i = 0; i < edges.length; i++) {
            FindEulerCycle.Edge newEdge = new FindEulerCycle.Edge(i, edges[i][0] - 1, edges[i][1] - 1);
            vertices[newEdge.getSourceVertexid()].addOutgoingEdge(newEdge);
            vertices[newEdge.getDestinationVertexId()].addInboundEdge(newEdge);
        }
        return new FindEulerCycle.Graph(vertices);
    }

    private static void validatePath(FindEulerCycle.Graph graph, FindEulerCycle.Path actualPath) {
        TestEdgeHelper helper = new TestEdgeHelper();
        for (FindEulerCycle.Vertex vertex : graph.getVertices()) {
            helper.addEdges(vertex.getOutgoingEdges());
        }
        Map<TestEdge, Integer> edgeCounter = helper.getEdgeCounter();

        List<FindEulerCycle.Vertex> pathVertices = actualPath.listPathVertices();
        for (int i = 0; i < pathVertices.size() - 1; i++) {
            TestEdge pathTestEdge = new TestEdge(pathVertices.get(i).getId(), pathVertices.get(i + 1).getId());
            Integer edgeCount = edgeCounter.get(pathTestEdge);

            if (edgeCount == null || edgeCount == 0) {
                fail(pathTestEdge + " does not exist or all edges have been used");
            } else {
                edgeCounter.put(pathTestEdge, edgeCount - 1);
            }
        }

        TestEdge lastEdge = new TestEdge(pathVertices.get(pathVertices.size() - 1).getId(), pathVertices.get(0).getId());
        Integer edgeCount = edgeCounter.get(lastEdge);
        if (edgeCount == null || edgeCount == 0) {
            fail(lastEdge + " does not exist or all edges have been used");
        } else {
            edgeCounter.put(lastEdge, edgeCount - 1);
        }

        for (Map.Entry<TestEdge, Integer> counter : edgeCounter.entrySet()) {
            assertTrue("Counter for edge " + counter.getKey() + " is " + counter.getValue(), counter.getValue() == 0);
        }
    }

    // Stress testing helper methods
    private static FindEulerCycle.Graph generateGraph(int n) {
        FindEulerCycle.Vertex[] vertices = new FindEulerCycle.Vertex[n];
        for (int i = 0; i < n; i++) {
            vertices[i] = new FindEulerCycle.Vertex(i);
        }

        // Create Hamiltonian path
        int edgeCoint = 0;
        for (int i = 0; i < n - 1; i++) {
            FindEulerCycle.Edge edge = new FindEulerCycle.Edge(edgeCoint++, i, i + 1);
            vertices[i].addOutgoingEdge(edge);
            vertices[i + 1].addInboundEdge(edge);
        }
        FindEulerCycle.Edge edge = new FindEulerCycle.Edge(edgeCoint++, n - 1, 0);
        vertices[n - 1].addOutgoingEdge(edge);
        vertices[0].addInboundEdge(edge);

        // Add cycle
        ThreadLocalRandom localRandom = ThreadLocalRandom.current();
        for (int c = 0; c < localRandom.nextInt(1, n); c++) {
            int cycleLength = localRandom.nextInt(2, n);
            int cycleStart = localRandom.nextInt(n);
            for (int i = 0; i < cycleLength - 1; i++) {
                int source = cycleStart + i > n - 1 ? cycleStart + i - n : cycleStart + i;
                int destination = cycleStart + i + 1 > n - 1 ? cycleStart + i + 1 - n : cycleStart + i + 1;
                FindEulerCycle.Edge cycleEdge =
                        new FindEulerCycle.Edge(edgeCoint++, source, destination);
                vertices[source].addOutgoingEdge(cycleEdge);
                vertices[destination].addInboundEdge(cycleEdge);
            }
            int source = cycleStart + cycleLength - 1 > n - 1 ? cycleStart + cycleLength - n - 1 : cycleStart + cycleLength - 1;
            int destination = cycleStart;
            FindEulerCycle.Edge cycleEdge =
                    new FindEulerCycle.Edge(edgeCoint++, source, destination);
            vertices[source].addOutgoingEdge(cycleEdge);
            vertices[destination].addInboundEdge(cycleEdge);
        }

        return new FindEulerCycle.Graph(vertices);
    }

    @Test
    public void testHasEulerPath_pathExists() throws Exception {

        FindEulerCycle.Vertex vertex1 = new FindEulerCycle.Vertex(0);
        FindEulerCycle.Edge edge1 = new FindEulerCycle.Edge(0, 0, 1);
        FindEulerCycle.Edge edge2 = new FindEulerCycle.Edge(1, 0, 0);
        FindEulerCycle.Edge edge3 = new FindEulerCycle.Edge(2, 1, 0);
        vertex1.addOutgoingEdge(edge1);
        vertex1.addOutgoingEdge(edge2);
        vertex1.addInboundEdge(edge2);
        vertex1.addInboundEdge(edge3);
        FindEulerCycle.Vertex vertex2 = new FindEulerCycle.Vertex(1);
        vertex2.addInboundEdge(edge1);
        vertex2.addOutgoingEdge(edge3);

        FindEulerCycle.Vertex[] vertices = new FindEulerCycle.Vertex[]{vertex1, vertex2};
        FindEulerCycle.Graph graph = new FindEulerCycle.Graph(vertices);
        assertTrue(solver.hasEulerCycle(graph));
    }

    @Test
    public void testHasEulerPath_pathExists_singleVertex() throws Exception {

        FindEulerCycle.Vertex vertex1 = new FindEulerCycle.Vertex(0);
        FindEulerCycle.Edge edge1 = new FindEulerCycle.Edge(0, 0, 0);
        vertex1.addInboundEdge(edge1);
        vertex1.addOutgoingEdge(edge1);

        FindEulerCycle.Vertex[] vertices = new FindEulerCycle.Vertex[]{vertex1};
        FindEulerCycle.Graph graph = new FindEulerCycle.Graph(vertices);
        assertTrue(solver.hasEulerCycle(graph));
    }

    @Test
    public void testHasEulerPath_NoPathExists() throws Exception {

        FindEulerCycle.Vertex vertex1 = new FindEulerCycle.Vertex(0);
        FindEulerCycle.Edge edge1 = new FindEulerCycle.Edge(0, 0, 1);
        FindEulerCycle.Edge edge2 = new FindEulerCycle.Edge(1, 0, 1);
        FindEulerCycle.Edge edge3 = new FindEulerCycle.Edge(2, 1, 0);
        vertex1.addOutgoingEdge(edge1);
        vertex1.addOutgoingEdge(edge2);
        vertex1.addInboundEdge(edge3);
        FindEulerCycle.Vertex vertex2 = new FindEulerCycle.Vertex(1);
        vertex2.addInboundEdge(edge1);
        vertex2.addInboundEdge(edge2);
        vertex2.addOutgoingEdge(edge3);

        FindEulerCycle.Vertex[] vertices = new FindEulerCycle.Vertex[]{vertex1, vertex2};
        FindEulerCycle.Graph graph = new FindEulerCycle.Graph(vertices);
        assertFalse(solver.hasEulerCycle(graph));
    }

    @Test
    public void testPathBuild_addSingleStep() {
        FindEulerCycle.Path path = new FindEulerCycle.Path();
        FindEulerCycle.Vertex vertex1 = new FindEulerCycle.Vertex(1);
        path.addStep(vertex1);
        FindEulerCycle.Step firstStep = path.getFirstStep();
        FindEulerCycle.Step lastStep = path.getLastStep();

        assertEquals(vertex1, firstStep.getVertex());
        assertNull(firstStep.getNextStep());
        assertEquals(vertex1, lastStep.getVertex());
        assertNull(lastStep.getNextStep());
    }

    @Test
    public void testPathBuild_addMultipleSteps() {
        FindEulerCycle.Path path = new FindEulerCycle.Path();
        FindEulerCycle.Vertex vertex1 = new FindEulerCycle.Vertex(1);
        path.addStep(vertex1);
        FindEulerCycle.Vertex vertex2 = new FindEulerCycle.Vertex(2);
        path.addStep(vertex2);

        FindEulerCycle.Step firstStep = path.getFirstStep();
        FindEulerCycle.Step lastStep = path.getLastStep();

        assertEquals(vertex1, firstStep.getVertex());
        assertEquals(lastStep, firstStep.getNextStep());
        assertEquals(vertex2, lastStep.getVertex());
        assertNull(lastStep.getNextStep());
    }

    @Test
    public void testPathBuild_completeCycle() {
        FindEulerCycle.Path path = new FindEulerCycle.Path();
        FindEulerCycle.Vertex vertex1 = new FindEulerCycle.Vertex(1);
        path.addStep(vertex1);
        FindEulerCycle.Vertex vertex2 = new FindEulerCycle.Vertex(2);
        path.addStep(vertex2);

        path.completeCycle();

        FindEulerCycle.Step firstStep = path.getFirstStep();
        FindEulerCycle.Step lastStep = path.getLastStep();

        assertEquals(vertex1, firstStep.getVertex());
        assertEquals(lastStep, firstStep.getNextStep());
        assertEquals(vertex2, lastStep.getVertex());
        assertEquals(firstStep, lastStep.getNextStep());
    }

    @Test
    public void testPathBuild_assignFirstStep() {
        FindEulerCycle.Path path = new FindEulerCycle.Path();
        FindEulerCycle.Vertex vertex1 = new FindEulerCycle.Vertex(1);
        path.addStep(vertex1);
        FindEulerCycle.Vertex vertex2 = new FindEulerCycle.Vertex(2);
        path.addStep(vertex2);
        FindEulerCycle.Vertex vertex3 = new FindEulerCycle.Vertex(3);
        path.addStep(vertex3);

        path.assignFirstStep(vertex2);

        FindEulerCycle.Step firstStep = path.getFirstStep();

        assertEquals(vertex2, firstStep.getVertex());
        assertEquals(vertex3, firstStep.getNextStep().getVertex());
    }

    @Test
    public void testListPathVertices_pathWithoutCycle() {
        FindEulerCycle.Path path = new FindEulerCycle.Path();
        FindEulerCycle.Vertex vertex1 = new FindEulerCycle.Vertex(1);
        path.addStep(vertex1);
        FindEulerCycle.Vertex vertex2 = new FindEulerCycle.Vertex(2);
        path.addStep(vertex2);
        FindEulerCycle.Vertex vertex3 = new FindEulerCycle.Vertex(3);
        path.addStep(vertex3);

        List<FindEulerCycle.Vertex> pathVertices = path.listPathVertices();
        List<FindEulerCycle.Vertex> expectedPathVertices = Arrays.asList(vertex1, vertex2, vertex3);
        assertEquals(expectedPathVertices, pathVertices);
    }

    @Test
    public void testListPathVertices_pathWithCycle() {
        FindEulerCycle.Path path = new FindEulerCycle.Path();
        FindEulerCycle.Vertex vertex1 = new FindEulerCycle.Vertex(1);
        path.addStep(vertex1);
        FindEulerCycle.Vertex vertex2 = new FindEulerCycle.Vertex(2);
        path.addStep(vertex2);
        FindEulerCycle.Vertex vertex3 = new FindEulerCycle.Vertex(3);
        path.addStep(vertex3);

        path.completeCycle();

        List<FindEulerCycle.Vertex> pathVertices = path.listPathVertices();
        List<FindEulerCycle.Vertex> expectedPathVertices = Arrays.asList(vertex1, vertex2, vertex3);
        assertEquals(expectedPathVertices, pathVertices);
    }

    @Test
    public void testListPathVertices_pathWithCycle_firstStepReassigned() {
        FindEulerCycle.Path path = new FindEulerCycle.Path();
        FindEulerCycle.Vertex vertex1 = new FindEulerCycle.Vertex(1);
        path.addStep(vertex1);
        FindEulerCycle.Vertex vertex2 = new FindEulerCycle.Vertex(2);
        path.addStep(vertex2);
        FindEulerCycle.Vertex vertex3 = new FindEulerCycle.Vertex(3);
        path.addStep(vertex3);

        path.completeCycle();
        path.assignFirstStep(vertex2);

        List<FindEulerCycle.Vertex> pathVertices = path.listPathVertices();
        List<FindEulerCycle.Vertex> expectedPathVertices = Arrays.asList(vertex2, vertex3, vertex1);
        assertEquals(expectedPathVertices, pathVertices);
    }

    @Test
    public void testListPathVertices_addVertexAfterCompletingCycle() {
        FindEulerCycle.Path path = new FindEulerCycle.Path();
        FindEulerCycle.Vertex vertex1 = new FindEulerCycle.Vertex(1);
        path.addStep(vertex1);
        FindEulerCycle.Vertex vertex2 = new FindEulerCycle.Vertex(2);
        path.addStep(vertex2);
        FindEulerCycle.Vertex vertex3 = new FindEulerCycle.Vertex(3);
        path.addStep(vertex3);

        path.completeCycle();
        path.assignFirstStep(vertex2);

        FindEulerCycle.Vertex vertex4 = new FindEulerCycle.Vertex(4);
        path.addStep(vertex4);

        List<FindEulerCycle.Vertex> pathVertices = path.listPathVertices();
        List<FindEulerCycle.Vertex> expectedPathVertices = Arrays.asList(vertex2, vertex3, vertex1, vertex2, vertex4);
        assertEquals(expectedPathVertices, pathVertices);
    }

    @Test
    public void testFindEulerianCycle_pathDoesNotExist() {
        FindEulerCycle.Vertex[] vertices = new FindEulerCycle.Vertex[3];
        vertices[0] = new FindEulerCycle.Vertex(0);
        vertices[1] = new FindEulerCycle.Vertex(1);
        vertices[2] = new FindEulerCycle.Vertex(2);

        FindEulerCycle.Edge edge_01 = new FindEulerCycle.Edge(0, 0, 1);
        vertices[0].addOutgoingEdge(edge_01);
        vertices[1].addInboundEdge(edge_01);
        FindEulerCycle.Edge edge_02 = new FindEulerCycle.Edge(1, 0, 2);
        vertices[0].addOutgoingEdge(edge_02);
        vertices[2].addInboundEdge(edge_02);

        FindEulerCycle.Edge edge_12 = new FindEulerCycle.Edge(2, 1, 2);
        vertices[1].addOutgoingEdge(edge_12);
        vertices[2].addInboundEdge(edge_12);

        FindEulerCycle.Edge edge_20 = new FindEulerCycle.Edge(3, 2, 0);
        vertices[2].addOutgoingEdge(edge_20);
        vertices[0].addInboundEdge(edge_20);

        FindEulerCycle.Graph graph = new FindEulerCycle.Graph(vertices);

        FindEulerCycle.Path path = solver.buildPath(graph);

        assertEquals(FindEulerCycle.EMPTY_PAtH, path);
    }

    @Test
    public void testFindEulerianCycle_pathDoesNotExist_isolatedVertex() {
        FindEulerCycle.Vertex[] vertices = new FindEulerCycle.Vertex[3];
        vertices[0] = new FindEulerCycle.Vertex(0);
        vertices[1] = new FindEulerCycle.Vertex(1);
        vertices[2] = new FindEulerCycle.Vertex(2);

        FindEulerCycle.Edge edge_01 = new FindEulerCycle.Edge(0, 0, 1);
        vertices[0].addOutgoingEdge(edge_01);
        vertices[1].addInboundEdge(edge_01);

        FindEulerCycle.Edge edge_10 = new FindEulerCycle.Edge(1, 1, 0);
        vertices[1].addOutgoingEdge(edge_10);
        vertices[0].addInboundEdge(edge_10);

        FindEulerCycle.Graph graph = new FindEulerCycle.Graph(vertices);

        FindEulerCycle.Path path = solver.buildPath(graph);

        assertEquals(FindEulerCycle.EMPTY_PAtH, path);
    }

    @Test
    public void testFindEulerianCycle_pathDoesNotExist_notStronglyConnected() {
        FindEulerCycle.Vertex[] vertices = new FindEulerCycle.Vertex[4];
        vertices[0] = new FindEulerCycle.Vertex(0);
        vertices[1] = new FindEulerCycle.Vertex(1);
        vertices[2] = new FindEulerCycle.Vertex(2);
        vertices[3] = new FindEulerCycle.Vertex(3);

        FindEulerCycle.Edge edge_01 = new FindEulerCycle.Edge(0, 0, 1);
        vertices[0].addOutgoingEdge(edge_01);
        vertices[1].addInboundEdge(edge_01);

        FindEulerCycle.Edge edge_10 = new FindEulerCycle.Edge(1, 1, 0);
        vertices[1].addOutgoingEdge(edge_10);
        vertices[0].addInboundEdge(edge_10);

        FindEulerCycle.Edge edge_23 = new FindEulerCycle.Edge(2, 2, 3);
        vertices[2].addOutgoingEdge(edge_23);
        vertices[3].addInboundEdge(edge_23);

        FindEulerCycle.Edge edge_32 = new FindEulerCycle.Edge(3, 3, 2);
        vertices[3].addOutgoingEdge(edge_32);
        vertices[2].addInboundEdge(edge_32);

        FindEulerCycle.Graph graph = new FindEulerCycle.Graph(vertices);

        FindEulerCycle.Path path = solver.buildPath(graph);

        assertEquals(FindEulerCycle.EMPTY_PAtH, path);
    }

    @Test
    public void testFindEulerianCycle_sample1_pathExists() {
        int[][] edgesArray = new int[][]{{2, 3},
                {2, 2},
                {1, 2},
                {3, 1}};
        FindEulerCycle.Graph graph = buildGraph(3, 4, edgesArray);

        FindEulerCycle.Path path = solver.buildPath(graph);

        validatePath(graph, path);
    }

    @Test
    public void testFindEulerianCycle_sample3_pathExists() {
        int[][] edgesArray = new int[][]{{1, 2},
                {2, 1},
                {1, 4},
                {4, 1},
                {2, 4},
                {3, 2},
                {4, 3}};
        FindEulerCycle.Graph graph = buildGraph(4, 7, edgesArray);

        FindEulerCycle.Path path = solver.buildPath(graph);

        validatePath(graph, path);
    }

    @Test
    public void testFindEulerianCycle_sample4_pathExists() {
        int[][] edgesArray = new int[][]{{2, 3},
                {3, 4},
                {1, 4},
                {3, 1},
                {4, 2},
                {2, 3},
                {4, 2}};
        FindEulerCycle.Graph graph = buildGraph(4, 7, edgesArray);

        FindEulerCycle.Path path = solver.buildPath(graph);

        validatePath(graph, path);
    }

    @Test
    public void testFindEulerianCycle_singleVertexGraph() {
        int[][] edgesArray = new int[][]{{1, 1}};
        FindEulerCycle.Graph graph = buildGraph(1, 1, edgesArray);

        FindEulerCycle.Path path = solver.buildPath(graph);

        validatePath(graph, path);
    }

    @Test
    public void testFindEulerianCycle_doubleCycleGraph() {
        int[][] edgesArray = new int[][]{{1, 2}, {1, 2}, {2, 1}, {2, 1}};
        FindEulerCycle.Graph graph = buildGraph(2, 4, edgesArray);

        FindEulerCycle.Path path = solver.buildPath(graph);

        validatePath(graph, path);
    }

    @Test
    public void testFindEulerianCycle_doubleCycleGraphWithSelfLoopf() {
        int[][] edgesArray = new int[][]{{1, 2}, {1, 2}, {2, 1}, {2, 1}, {1, 1}, {2, 2}};
        FindEulerCycle.Graph graph = buildGraph(2, 6, edgesArray);

        FindEulerCycle.Path path = solver.buildPath(graph);

        validatePath(graph, path);
    }

    @Test
    public void testFindEulerianCycle_generated10() {
        for (int i = 0; i < 25; i++) {
            FindEulerCycle.Graph graph = generateGraph(10);

            FindEulerCycle.Path path = solver.buildPath(graph);

            validatePath(graph, path);
        }
    }

    @Test
    public void testFindEulerianCycle_generated100() {
        FindEulerCycle.Graph graph = generateGraph(100);

        FindEulerCycle.Path path = solver.buildPath(graph);

        validatePath(graph, path);
    }

    @Test
    public void testFindEulerianCycle_generated1000() {
        FindEulerCycle.Graph graph = generateGraph(1000);

        FindEulerCycle.Path path = solver.buildPath(graph);

        validatePath(graph, path);
    }

    static class TestEdge {
        private final int source;
        private final int destination;


        TestEdge(int source, int destination) {
            this.source = source;
            this.destination = destination;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestEdge testEdge = (TestEdge) o;

            if (source != testEdge.source) return false;
            return destination == testEdge.destination;
        }

        @Override
        public int hashCode() {
            int result = source;
            result = 31 * result + destination;
            return result;
        }

        @Override
        public String toString() {
            return "TestEdge{" +
                    "source=" + source +
                    ", destination=" + destination +
                    '}';
        }
    }

    static class TestEdgeHelper {
        private final Map<TestEdge, Integer> edgeCounter;

        TestEdgeHelper() {
            edgeCounter = new HashMap<>();
        }

        public void addEdges(Collection<FindEulerCycle.Edge> edges) {
            for (FindEulerCycle.Edge edge : edges) {
                TestEdge testEdge = new TestEdge(edge.getSourceVertexid(), edge.getDestinationVertexId());
                Integer currentCount = edgeCounter.get(testEdge);
                if (currentCount == null) {
                    edgeCounter.put(testEdge, 1);
                } else {
                    edgeCounter.put(testEdge, currentCount + 1);
                }
            }
        }

        public Map<TestEdge, Integer> getEdgeCounter() {
            return edgeCounter;
        }
    }
}