package week3;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Solution for problem 3 of week 3.
 * Bubble Detection
 */
public class BubbleDetection {
    public static void main(String... args) {
        Scanner stdin = new Scanner(new BufferedInputStream(System.in));
        Set<String> reads = new HashSet<>();
        int k = stdin.nextInt();
        int t = stdin.nextInt();

        while (stdin.hasNext()) {
            reads.add(stdin.nextLine());
        }

        BubbleDetection solver = new BubbleDetection();
        System.out.println(solver.countBubbles(k, t, reads));
    }

    int countBubbles(int k, int t, Collection<String> reads) {
        Set<String> kmers = new HashSet<>();
        for (String read : reads) {
            generateKmers(read, k, kmers);
        }

        DeBruijnGraph deBruijnGraph = buildDeBruijnGraph(kmers);
        Map<String, List<VertexTraversal>> traversals = new HashMap<>(deBruijnGraph.getVertices().size());
        for (Vertex vertex : deBruijnGraph.getVertices()) {
            if (vertex.getOutgoingEdges().size() > 1) {
                buildVerticesTraversals(t, vertex, deBruijnGraph, traversals);
            }
        }
        int bubbles = 0;
        for (List<VertexTraversal> vertexTraversals : traversals.values()) {
            if (vertexTraversals.size() > 1) {
                bubbles += countDisjoints(vertexTraversals);
            }
        }
        return bubbles;
    }

    private int countDisjoints(List<VertexTraversal> vertexTraversals) {
        int disjointsCount = 0;
        for (int i = 0; i < vertexTraversals.size() - 1; i++) {
            VertexTraversal traversal1 = vertexTraversals.get(i);
            for (int j = i + 1; j < vertexTraversals.size(); j++) {
                if (traversal1.isDisjoint(vertexTraversals.get(j))) {
                    disjointsCount++;
                }
            }
        }
        return disjointsCount;
    }


    private void buildVerticesTraversals(
            int t, Vertex source, DeBruijnGraph graph, Map<String, List<VertexTraversal>> traversals) {
        List<VertexTraversal> levelTraversals = new LinkedList<>();
        levelTraversals.add(new VertexTraversal(source));

        for (int i = 0; i < t && !levelTraversals.isEmpty(); i++) {
            List<VertexTraversal> nextLevel = new ArrayList<>();

            for (VertexTraversal vertexTraversal : levelTraversals) {
                for (Edge edge : vertexTraversal.getVertex().getOutgoingEdges()) {
                    Vertex destinationVertex = graph.getVertexMap().get(edge.getDestinationVertexId());
                    if (!vertexTraversal.getVertices().contains(destinationVertex)) {
                        VertexTraversal traversal = vertexTraversal.append(destinationVertex, edge);
                        nextLevel.add(traversal);
                        addVertexTraversal(traversal, traversals);
                    }
                }
            }
            levelTraversals = nextLevel;
        }
    }

    private void addVertexTraversal(VertexTraversal traversal, Map<String, List<VertexTraversal>> destination) {
        Collection<VertexTraversal> traversals =
                destination.computeIfAbsent(traversal.getVertex().getId(), k -> new ArrayList<>());
        traversals.add(traversal);
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Vertex vertex = (Vertex) o;

            return getId().equals(vertex.getId());
        }

        @Override
        public int hashCode() {
            return getId().hashCode();
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

    static class VertexTraversal {
        private final Vertex source;
        private final Vertex vertex;
        private final Set<Vertex> vertices;

        VertexTraversal(Vertex vertex) {
            this.source = vertex;
            this.vertex = vertex;
            this.vertices = Collections.singleton(vertex);
        }

        private VertexTraversal(Vertex source, Vertex vertex, Set<Vertex> vertices) {
            this.source = source;
            this.vertex = vertex;
            this.vertices = vertices;
        }

        public Vertex getVertex() {
            return vertex;
        }

        public Set<Vertex> getVertices() {
            return vertices;
        }

        VertexTraversal append(Vertex vertex, Edge edge) {
            Set<Vertex> newVertices = new HashSet<>(this.getVertices());
            newVertices.add(vertex);
            return new VertexTraversal(this.source, vertex, newVertices);
        }

        boolean isDisjoint(VertexTraversal other) {
            boolean isSameSource = this.source.getId().equals(other.source.getId());
            boolean isSameDestination = this.getVertex().getId().equals(other.getVertex().getId());
            int countEqualVertices = 0;
            for (Vertex otherVertex : other.getVertices()) {
                if (this.getVertices().contains(otherVertex)) {
                    countEqualVertices++;
                }
            }
            return isSameSource && isSameDestination && countEqualVertices == 2;
        }
    }

    static class FastScanner {
        private BufferedReader reader;
        private StringTokenizer tokenizer;

        FastScanner() {
            reader = new BufferedReader(new InputStreamReader(System.in));
            tokenizer = null;
        }

        String next() throws IOException {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                tokenizer = new StringTokenizer(reader.readLine());
            }
            return tokenizer.nextToken();
        }

        int nextInt() throws IOException {
            return Integer.parseInt(next());
        }
    }
}
