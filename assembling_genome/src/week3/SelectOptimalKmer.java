package week3;

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
 * Solution for problem 2 of week 3.
 * Selecting the Optimal 𝑘-mer Size
 */
public class SelectOptimalKmer {
    private static final int DEFAULT_READS_NUMBER = 400;

    public static void main(String... args) {
        Scanner stdin = new Scanner(new BufferedInputStream(System.in));
        Set<String> reads = new HashSet<>();
        int readsCounter = 0;
        while (readsCounter++ < DEFAULT_READS_NUMBER && stdin.hasNext()) {
            reads.add(stdin.nextLine());
        }

        SelectOptimalKmer solver = new SelectOptimalKmer();
        int optimalK = solver.findOptimalK(reads);

        System.out.println(optimalK);
    }

    /**
     * Solve the problem of finding an optimal kmer size for a provided set of reads.
     * Uses brute-force approach to define the maximum k value,
     * it iteratively decreases k size until Eulerian cycle in DeBrujin graph for the generated kmers exists.
     */
    int findOptimalK(Set<String> reads) {
        int k = reads.iterator().next().length();
        while (k > 1) {
            Set<String> kmers = new HashSet<>();
            for (String read : reads) {
                generateKmers(read, k, kmers);
            }

            DeBruijnGraph deBruijnGraph = buildDeBruijnGraph(kmers);
            if (hasEulerCycle(deBruijnGraph)) {
                return k;
            }
            k--;
        }
        return 0;
    }

    /**
     * Creates k-mers of size k from the provided read and puts them into kmers set.
     */
    private void generateKmers(String read, int k, Set<String> kmers) {
        for (int i = 0; i + k <= read.length(); i++) {
            kmers.add(read.substring(i, i + k));
        }
    }

    private DeBruijnGraph buildDeBruijnGraph(Collection<String> kmers) {
        DeBruijnGraph result = new DeBruijnGraph();
        for (String kmer : kmers) {
            result.addKMer(kmer);
        }
        return result;
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

    /**
     * Creates transposed graph for the provided graph object.
     */
    private DeBruijnGraph transposeGraph(DeBruijnGraph graph) {
        DeBruijnGraph result = new DeBruijnGraph();
        for (Vertex vertex : graph.getVertices()) {
            Vertex resultVertex = new Vertex(vertex.getId());
            for (Edge inEdge : vertex.getInboundEdges()) {
                resultVertex.addOutgoingEdge(
                        new Edge(
                                inEdge.getId(),
                                inEdge.getName(),
                                inEdge.getDestinationVertexId(),
                                inEdge.getSourceVertexId()));
            }
            for (Edge outEdge : vertex.getOutgoingEdges()) {
                resultVertex.addInboundEdge((
                        new Edge(
                                outEdge.getId(),
                                outEdge.getName(),
                                outEdge.getDestinationVertexId(),
                                outEdge.getSourceVertexId())));
            }
            result.getVertexMap().put(vertex.getId(), resultVertex);
        }

        return result;
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
}
