package week2;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Solution for problem 2 of week 2.
 * Finding an Eulerian Cycle in Directed Graph
 */
public class FindEulerCycle {
    static final Path EMPTY_PAtH = new Path();

    public static void main(String... args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int m = scanner.nextInt();

        Vertex[] vertices = new Vertex[n];
        for (int i = 0; i < m; i++) {
            int source, destination;
            source = scanner.nextInt();
            destination = scanner.nextInt();
            Edge edge = new Edge(i, source - 1, destination - 1);

            Vertex sourceVertex = vertices[source - 1];
            if (sourceVertex == null) {
                sourceVertex = new Vertex(source - 1);
                vertices[source - 1] = sourceVertex;
            }
            sourceVertex.getOutgoingEdges().add(edge);

            Vertex destinationVertex = vertices[destination - 1];
            if (destinationVertex == null) {
                destinationVertex = new Vertex(destination - 1);
                vertices[destination - 1] = destinationVertex;
            }
            destinationVertex.getInboundEdges().add(edge);
        }
        Graph graph = new Graph(vertices);

        FindEulerCycle solver = new FindEulerCycle();
        Path path = solver.buildPath(graph);

        System.out.println(path.toString());
    }

    /**
     * Builds a Eulerian path(cycle) for the provided graph.
     */
    Path buildPath(Graph graph) {
        if (!hasEulerCycle(graph)) {
            return EMPTY_PAtH;
        }

        Path path = new Path();
        Deque<Edge> edgeStack = new LinkedList<>();
        Set<Integer> usedEdges = new HashSet<>();
        Vertex vertex = graph.getVertices()[0];
        path.addStep(vertex);
        edgeStack.addAll(vertex.getOutgoingEdges());
        boolean setNewStart = false;

        while (!edgeStack.isEmpty()) {
            Edge edge = edgeStack.pop();
            usedEdges.add(edge.getId());

            Vertex destinationVertex = graph.getVertices()[edge.getDestinationVertexId()];
            List<Edge> outgoingEdges = destinationVertex.getOutgoingEdges();
            boolean nextMoveFound = false;
            for (Edge outgoingEdge : outgoingEdges) {
                if (!usedEdges.contains(outgoingEdge.getId())) {
                    if (outgoingEdge.isSelfLoop()) {
                        path.addStep(destinationVertex);
                        usedEdges.add(outgoingEdge.getId());
                    } else {
                        nextMoveFound = true;
                        edgeStack.push(outgoingEdge);
                    }
                }
            }
            if (nextMoveFound) {
                if (setNewStart) {
                    path.assignFirstStep(graph.getVertices()[edge.getSourceVertexid()]);
                    setNewStart = false;
                }
                path.addStep(destinationVertex);
            } else {
                // Based on the Euler theorem proof, vertex has no available outgoing edges if it is a start point of the current path,
                // i.e. source vertex for the next available edge should be set as a start vertex of the path.
                path.completeCycle();
                setNewStart = true;
            }
        }
        return path;
    }

    /**
     * Checks if the given graph has Eulerian cycle.
     * According to the Euler theorem directed graph has a cycle if it is balanced and strongly connected.
     */
    boolean hasEulerCycle(Graph graph) {
        for (Vertex vertex : graph.getVertices()) {
            if (vertex.getInboundEdges().size() == 0 ||
                    vertex.getInboundEdges().size() != vertex.getOutgoingEdges().size()) {
                return false;
            }
        }
        return isStronglyConected(graph);
    }

    /**
     * Checks if the provided graph is a strongly connected (all vertices belong to a single strongly connected component).
     */
    private boolean isStronglyConected(Graph graph) {
        boolean[] visited = visitGraphVertices(graph);
        for (boolean isVisited : visited) {
            if (!isVisited) {
                return false;
            }
        }

        Graph transposedGraph = transposeGraph(graph);
        boolean[] visitedTransposed = visitGraphVertices(transposedGraph);
        for (boolean isVisited : visitedTransposed) {
            if (!isVisited) {
                return false;
            }
        }

        return true;
    }

    /**
     * Creates transposed graph for the provided graph object.
     */
    private Graph transposeGraph(Graph graph) {
        Vertex[] resultVertices = new Vertex[graph.getVertices().length];
        for (int i = 0; i < resultVertices.length; i++) {
            resultVertices[i] = new Vertex(i);
            Vertex inVertex = graph.getVertices()[i];
            for (Edge inEdge : inVertex.getInboundEdges()) {
                resultVertices[i].addOutgoingEdge(
                        new Edge(inEdge.getId(), inEdge.getDestinationVertexId(), inEdge.getSourceVertexid()));
            }
            for (Edge outEdge : inVertex.getOutgoingEdges()) {
                resultVertices[i].addInboundEdge((
                        new Edge(outEdge.getId(), outEdge.getDestinationVertexId(), outEdge.getSourceVertexid())));
            }
        }
        return new Graph(resultVertices);
    }

    /**
     * Iterate over graph vertices using DFS.
     */
    private boolean[] visitGraphVertices(Graph graph) {
        boolean[] visited = new boolean[graph.getVertices().length];
        Deque<Vertex> stack = new LinkedList<>();
        stack.add(graph.getVertices()[0]);

        while (!stack.isEmpty()) {
            Vertex vertex = stack.pop();
            visited[vertex.getId()] = true;

            for (Edge edge : vertex.getOutgoingEdges()) {
                if (!visited[edge.getDestinationVertexId()]) {
                    stack.push(graph.getVertices()[edge.getDestinationVertexId()]);
                }
            }
        }
        return visited;
    }

    /**
     * Class representing a directed graph.
     */
    static class Graph {
        private final Vertex[] vertices;

        public Graph(Vertex[] vertices) {
            this.vertices = vertices;
        }

        Vertex[] getVertices() {
            return vertices;
        }
    }

    /**
     * Class representing a Vertex in {@link Graph}.
     */
    static class Vertex {
        private final int id;
        private final List<Edge> inboundEdges;
        private final List<Edge> outgoingEdges;

        Vertex(int id) {
            this.id = id;
            inboundEdges = new ArrayList<>();
            outgoingEdges = new ArrayList<>();
        }

        int getId() {
            return id;
        }

        List<Edge> getInboundEdges() {
            return inboundEdges;
        }

        List<Edge> getOutgoingEdges() {
            return outgoingEdges;
        }

        void addInboundEdge(Edge edge) {
            this.inboundEdges.add(edge);
        }

        void addOutgoingEdge(Edge edge) {
            this.outgoingEdges.add(edge);
        }
    }

    /**
     * Class representing edge in {@link Graph}.
     */
    static class Edge {
        private final int id;
        private final int sourceVertexid;
        private final int destinationVertexId;

        Edge(int id, int sourceVertexid, int destinationVertexId) {
            this.id = id;
            this.sourceVertexid = sourceVertexid;
            this.destinationVertexId = destinationVertexId;
        }

        int getId() {
            return id;
        }

        int getSourceVertexid() {
            return sourceVertexid;
        }

        int getDestinationVertexId() {
            return destinationVertexId;
        }

        boolean isSelfLoop() {
            return this.sourceVertexid == this.destinationVertexId;
        }
    }

    /**
     * Data structure for holding eulerian path for a directed graph.
     */
    static class Path {
        private Step firstStep;
        private Step lastStep;
        private Map<Integer, Step> indexedLastSteps;

        Path() {
            indexedLastSteps = new HashMap<>();
        }

        void addStep(Vertex vertex) {
            Step newStep = new Step(vertex);
            if (firstStep == null) {
                // Initial step
                firstStep = newStep;
                lastStep = newStep;
            } else if (lastStep.nextStep == firstStep) {
                // Add new step to the cycled path. First step vertex should be added into a path as a new step.
                Step newFirstStep = new Step(firstStep.getVertex());
                indexedLastSteps.put(firstStep.getVertex().getId(), newFirstStep);
                lastStep.nextStep = newFirstStep;
                newFirstStep.previousStep = lastStep;
                lastStep = newFirstStep;

                lastStep.nextStep = newStep;
                newStep.previousStep = lastStep;
                lastStep = newStep;
            } else {
                lastStep.nextStep = newStep;
                newStep.previousStep = lastStep;
                lastStep = newStep;
            }

            indexedLastSteps.put(vertex.getId(), newStep);
        }

        void completeCycle() {
            lastStep.nextStep = firstStep;
            firstStep.previousStep = lastStep;
        }

        void assignFirstStep(Vertex vertex) {
            Step latestStepWithVertex = indexedLastSteps.get(vertex.getId());
            firstStep = latestStepWithVertex;
            lastStep = latestStepWithVertex.previousStep;
        }

        List<Vertex> listPathVertices() {
            List<Vertex> result = new ArrayList<>();
            Step currentStep = firstStep;
            while (currentStep != null) {
                result.add(currentStep.getVertex());
                currentStep = currentStep.nextStep;
                if (currentStep == firstStep) {
                    break;
                }
            }
            return result;
        }

        @Override
        public String toString() {
            if (firstStep == null) {
                return "0";
            } else {
                StringBuilder result = new StringBuilder("1\n");
                for (Vertex vertex : listPathVertices()) {
                    result.append(vertex.getId() + 1).append(" ");
                }
                return result.toString();
            }
        }

        Step getFirstStep() {
            return this.firstStep;
        }

        Step getLastStep() {
            return this.lastStep;
        }
    }

    /**
     * Single step in eulerian path {@link Path}.
     */
    static class Step {
        private final Vertex vertex;
        private Step nextStep;
        private Step previousStep;

        Step(Vertex vertex) {
            this.vertex = vertex;
        }

        Vertex getVertex() {
            return vertex;
        }

        Step getNextStep() {
            return nextStep;
        }
    }
}
