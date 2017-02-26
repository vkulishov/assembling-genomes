package week2;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Solution for problem 3 of week 2.
 * Finding a 𝑘-Universal Circular String
 */
public class KUniversalCircularString {
    static final Path EMPTY_PAtH = new Path();

    public static void main(String... args) {
        Scanner stdin = new Scanner(new BufferedInputStream(System.in));
        int k = stdin.nextInt();

        KUniversalCircularString solver = new KUniversalCircularString();
        String kuniversalString = solver.findKuniversalString(k);

        System.out.println(kuniversalString);
    }

    /**
     * Finds a k-universal circular string for a binary string of length k
     */
    String findKuniversalString(int k) {
        List<String> kmers = buildKMers(k);
        DeBruijnGraph deBruijnGraph = buildDeBruijnGraph(kmers);
        Path path = buildPath(deBruijnGraph);

        StringBuilder result = new StringBuilder();
        for (Vertex vertex : path.listPathVertices()) {
            result.append(vertex.getId().charAt(vertex.getId().length() - 1));
        }

        return result.toString();
    }

    /**
     * Builds a Eulerian path(cycle) for the provided graph.
     */
    Path buildPath(DeBruijnGraph graph) {
        if (!hasEulerCycle(graph)) {
            return EMPTY_PAtH;
        }

        Path path = new Path();
        Deque<Edge> edgeStack = new LinkedList<>();
        Set<Integer> usedEdges = new HashSet<>();
        Vertex vertex = graph.getVertices().iterator().next();
        path.addStep(vertex);
        edgeStack.addAll(vertex.getOutgoingEdges());
        boolean setNewStart = false;

        while (!edgeStack.isEmpty()) {
            Edge edge = edgeStack.pop();
            usedEdges.add(edge.getId());

            Vertex destinationVertex = graph.getVertexMap().get(edge.getDestinationVertexId());
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
                    path.assignFirstStep(graph.getVertexMap().get(edge.getSourceVertexId()));
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
    private boolean hasEulerCycle(DeBruijnGraph graph) {
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
    private boolean isStronglyConected(DeBruijnGraph graph) {
        Map<String, Boolean> visited = visitGraphVertices(graph);
        for (boolean isVisited : visited.values()) {
            if (!isVisited) {
                return false;
            }
        }

        DeBruijnGraph transposedGraph = transposeGraph(graph);
        Map<String, Boolean> visitedTransposed = visitGraphVertices(transposedGraph);
        for (boolean isVisited : visitedTransposed.values()) {
            if (!isVisited) {
                return false;
            }
        }

        return true;
    }

    /**
     * Creates transposed graph for the provided graph object.
     */
    private DeBruijnGraph transposeGraph(DeBruijnGraph graph) {
        DeBruijnGraph result = new DeBruijnGraph();
        for (Vertex vertex : graph.getVertices()) {
            Vertex resultVertex = new Vertex(vertex.getId());
            for (Edge inEdge : vertex.getInboundEdges()) {
                resultVertex.addOutgoingEdge(
                        new Edge(inEdge.getId(), inEdge.getName(), inEdge.getDestinationVertexId(), inEdge.getSourceVertexId()));
            }
            for (Edge outEdge : vertex.getOutgoingEdges()) {
                resultVertex.addInboundEdge((
                        new Edge(outEdge.getId(), outEdge.getName(), outEdge.getDestinationVertexId(), outEdge.getSourceVertexId())));
            }
            result.getVertexMap().put(vertex.getId(), resultVertex);
        }

        return result;
    }

    /**
     * Iterate over graph vertices using DFS.
     */
    private Map<String, Boolean> visitGraphVertices(DeBruijnGraph graph) {
        Map<String, Boolean> visited = new HashMap<>(graph.getVertices().size());
        for (Vertex vertex : graph.getVertices()) {
            visited.put(vertex.getId(), false);
        }
        Deque<Vertex> stack = new LinkedList<>();
        stack.add(graph.getVertices().iterator().next());

        while (!stack.isEmpty()) {
            Vertex vertex = stack.pop();
            visited.put(vertex.getId(), true);

            for (Edge edge : vertex.getOutgoingEdges()) {
                if (!visited.get(edge.getDestinationVertexId())) {
                    stack.push(graph.getVertexMap().get(edge.getDestinationVertexId()));
                }
            }
        }
        return visited;
    }

    DeBruijnGraph buildDeBruijnGraph(List<String> kmers) {
        DeBruijnGraph result = new DeBruijnGraph();
        for (String kmer : kmers) {
            result.addKMer(kmer);
        }
        return result;
    }

    List<String> buildKMers(int k) {
        List<String> result = new ArrayList<>();
        generateKMers(k, result, "");
        return result;
    }

    /**
     * Recursively generate multiset of binary strings of size k.
     */
    private void generateKMers(int k, List<String> result, String kmer) {
        if (k == 1) {
            result.add(kmer + "0");
            result.add(kmer + "1");
            return;
        }
        generateKMers(k - 1, result, kmer + "0");
        generateKMers(k - 1, result, kmer + "1");
    }

    /**
     * Class representing a De Bruijn graph data structure built from a list of kmers.
     */
    static class DeBruijnGraph {
        private final Map<String, Vertex> vertexMap;
        private int edgeCounter;

        DeBruijnGraph() {
            vertexMap = new HashMap<>();
        }

        void addKMer(String kmer) {
            String leftPart = kmer.substring(0, kmer.length() - 1);
            String rightPart = kmer.substring(1, kmer.length());

            Edge edge = new Edge(edgeCounter++, kmer, leftPart, rightPart);
            if (leftPart.equals(rightPart)) {
                // Self-loop edge
                Vertex vertex = vertexMap.computeIfAbsent(leftPart, k -> new Vertex(leftPart));
                vertex.addOutgoingEdge(edge);
                vertex.addInboundEdge(edge);
            } else {
                Vertex leftVertex = vertexMap.computeIfAbsent(leftPart, k -> new Vertex(leftPart));
                leftVertex.addOutgoingEdge(edge);

                Vertex rightVertex = vertexMap.computeIfAbsent(rightPart, k -> new Vertex(rightPart));
                rightVertex.addInboundEdge(edge);
            }
        }

        Collection<Vertex> getVertices() {
            return vertexMap.values();
        }

        Map<String, Vertex> getVertexMap() {
            return vertexMap;
        }
    }

    /**
     * Class representing a Vertex in {@link DeBruijnGraph}.
     */
    static class Vertex {
        private final String id;
        private final List<Edge> inboundEdges;
        private final List<Edge> outgoingEdges;

        Vertex(String id) {
            this.id = id;
            inboundEdges = new ArrayList<>();
            outgoingEdges = new ArrayList<>();
        }

        String getId() {
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
     * Class representing edge in {@link DeBruijnGraph}.
     */
    static class Edge {
        private final int id;
        private final String name;
        private final String sourceVertexid;
        private final String destinationVertexId;

        Edge(int id, String name, String sourceVertexid, String destinationVertexId) {
            this.id = id;
            this.name = name;
            this.sourceVertexid = sourceVertexid;
            this.destinationVertexId = destinationVertexId;
        }

        int getId() {
            return id;
        }

        String getName() {
            return name;
        }

        String getSourceVertexId() {
            return sourceVertexid;
        }

        String getDestinationVertexId() {
            return destinationVertexId;
        }

        boolean isSelfLoop() {
            return this.sourceVertexid.equals(this.destinationVertexId);
        }
    }

    /**
     * Data structure for holding eulerian path for a directed graph.
     */
    static class Path {
        private Step firstStep;
        private Step lastStep;
        private Map<String, Step> indexedLastSteps;

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
                    result.append(vertex.getId()).append(" ");
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
