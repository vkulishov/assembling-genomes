package week3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.function.Predicate;

/**
 * Solution for problem 1 of week 3.
 * Finding a Circulation in a Network
 */
public class FindCirculation {
    private static final CirculationResponse NO_CIRCULATION_RESPONSE = new CirculationResponse();

    public static void main(String... args) throws IOException {
        FindCirculation solver = new FindCirculation();

        FlowNetworkGraph flowNetworkGraph = readGraph(new FastScanner());
        CirculationResponse circulation = solver.findCirculation(flowNetworkGraph);
        System.out.println(circulation.toString());
    }

    private static FlowNetworkGraph readGraph(FastScanner in) throws IOException {
        int vertexCount = in.nextInt();
        int edgeCount = in.nextInt();
        FlowNetworkGraph flowNetworkGraph = new FlowNetworkGraph(vertexCount, edgeCount);

        for (int i = 0; i < edgeCount; ++i) {
            int from = in.nextInt() - 1;
            int to = in.nextInt() - 1;
            int lowerBound = in.nextInt();
            int capacity = in.nextInt();
            flowNetworkGraph.addEdge(from, to, lowerBound, capacity);
        }
        return flowNetworkGraph;
    }

    /**
     * Solves the problem of finding circulation in a graph if exists.
     */
    CirculationResponse findCirculation(FlowNetworkGraph graph) {
        if (isFlowRequired(graph)) {
            reduceToNetworkFlowGraph(graph);
            maximizeFlow(graph, graph.getSource(), graph.getTarget());

            int sourceFlow = graph.getSource().getOutgoingEdges().stream().mapToInt(o -> o.flow).sum();
            // A circulation problem is feasible if and only if the max-flow value is D,
            // where D is a demand of source and target vertices.
            int expectedFlow = graph.getSource().getOutgoingEdges().stream().mapToInt(o -> o.capacity).sum();
            int targetFlow = graph.getTarget().getInboundEdges().stream().mapToInt(o -> o.flow).sum();
            if (sourceFlow != expectedFlow || sourceFlow != targetFlow) {
                return NO_CIRCULATION_RESPONSE;
            } else {
                List<Edge> initialEdges = graph.getInitialEdges();
                // Restore actual flow adding lower bound to flow
                for (Edge edge : initialEdges) {
                    edge.flow += edge.lowerBound;
                }
                return new CirculationResponse(initialEdges);
            }
        } else {
            return new CirculationResponse(graph.getInitialEdges());
        }
    }

    /**
     * Reduces the provided representing a circulation network into a network with source and target vertices.
     * Vertices that have negative demand are connected with a source and vertices with positive demand are
     * connected with target.
     * Demand of a vertex is defined as -1 * (sum(Input lower bounds) - sum(Out lower bounds));
     * Capacities of all edges are reduced to edge lower bound.
     */
    void reduceToNetworkFlowGraph(FlowNetworkGraph graph) {
        // Vertices with negative demand. Key is vertex id, value is demand.
        Map<Integer, Integer> semiSources = new HashMap<>();
        // Vertices with positive demand. Key is vertex id, value is demand.
        Map<Integer, Integer> semiSinks = new HashMap<>();

        for (Vertex vertex : graph.vertices) {
            int inLowerBound = vertex.inboundEdges.stream().mapToInt(o -> o.lowerBound).sum();
            int outLowerBound = vertex.outgoingEdges.stream().mapToInt(o -> o.lowerBound).sum();

            int vertexDemand = -1 * (inLowerBound - outLowerBound);
            if (vertexDemand < 0) {
                semiSources.put(vertex.id, -1 * vertexDemand);
            } else if (vertexDemand > 0) {
                semiSinks.put(vertex.id, vertexDemand);
            }
        }

        Vertex source = new Vertex(graph.getVertices().size());
        graph.setSource(source);
        for (Map.Entry<Integer, Integer> semiSource : semiSources.entrySet()) {
            graph.addEdge(source.id, semiSource.getKey(), semiSource.getValue());
        }

        Vertex target = new Vertex(graph.getVertices().size());
        graph.setTarget(target);
        for (Map.Entry<Integer, Integer> semiSink : semiSinks.entrySet()) {
            graph.addEdge(semiSink.getKey(), target.id, semiSink.getValue());
        }

        // Reduce edges capacity.
        graph.getEdges().forEach(edge -> edge.capacity -= edge.lowerBound);
    }

    /**
     * Checks if the provided graph has vertices with non-zero lower bound,
     * i.e if there are edges that require non-zero flow.
     */
    private boolean isFlowRequired(FlowNetworkGraph graph) {
        return graph.getEdges().stream().anyMatch(edge -> edge.getLowerBound() > 0);
    }

    /**
     * Find path from source vertex to target vertex in the provided graph using BFS.
     */
    FlowPath findFlowPath(FlowNetworkGraph graph, Vertex source, Vertex target) {
        boolean[] visited = new boolean[graph.getVertices().size()];
        Edge[] pathEdges = new Edge[graph.getVertices().size()];

        int pathFlow = Integer.MAX_VALUE;
        Deque<Vertex> queue = new ArrayDeque<>();
        queue.add(source);
        visited[source.id] = true;

        Vertex vertex = null;
        while (!queue.isEmpty() && vertex != target) {
            vertex = queue.pollFirst();
            for (Edge adjEdge : vertex.getOutgoingEdges()) {
                if (adjEdge.flow < adjEdge.capacity && !visited[adjEdge.to.id]) {
                    visited[adjEdge.to.id] = true;
                    pathEdges[adjEdge.to.id] = adjEdge;
                    pathFlow = Math.min(pathFlow, adjEdge.capacity - adjEdge.flow);
                    queue.add(adjEdge.to);
                }
            }
        }
        Deque<Edge> pathEdgesDequeue = new LinkedList<>();
        Edge edge = pathEdges[target.id];
        while (edge != null) {
            pathEdgesDequeue.addFirst(edge);
            edge = pathEdges[edge.from.id];
        }
        return new FlowPath(pathEdgesDequeue, pathFlow);
    }

    private void maximizeFlow(FlowNetworkGraph graph, Vertex source, Vertex target) {
        FlowPath path = findFlowPath(graph, source, target);
        while (!path.getEdges().isEmpty()) {
            for (Edge edge : path.getEdges()) {
                graph.addFlow(edge.getId(), path.getFlow());
            }
            path = findFlowPath(graph, source, target);
        }
    }

    /**
     * Class represents a flow in a graph.
     */
    static class FlowPath {
        private Deque<Edge> edges;
        private int flow;

        FlowPath(Deque<Edge> edges, int flow) {
            this.edges = edges;
            this.flow = flow;
        }

        Deque<Edge> getEdges() {
            return edges;
        }

        int getFlow() {
            return flow;
        }
    }

    /**
     * Class represents flow network for solving circulation problem.
     */
    static class FlowNetworkGraph {
        private final List<Vertex> vertices;
        private final List<Edge> edges;
        private final int m;
        private Vertex source;
        private Vertex target;
        private final List<Edge> forwardEdges;

        FlowNetworkGraph(int n, int m) {
            this.m = m;
            vertices = new ArrayList<>(n + 2); // keep space for 2 extra vertices, source and target.
            for (int i = 0; i < n; i++) {
                vertices.add(new Vertex(i));
            }
            edges = new ArrayList<>();
            forwardEdges = new ArrayList<>();
        }

        void addEdge(int from, int to, int lowerBound, int capacity) {
            /* Note that we first append a forward edge and then a backward edge,
             * so all forward edges are stored at even indices (starting from 0),
             * whereas backward edges are stored at odd indices. */
            Vertex fromVertex = vertices.get(from);
            Vertex toVertex = vertices.get(to);
            Edge forwardEdge = new Edge(edges.size(), fromVertex, toVertex, lowerBound, capacity);
            Edge backwardEdge = new Edge(edges.size() + 1, toVertex, fromVertex, 0, 0);

            fromVertex.addInboundEdge(backwardEdge);
            fromVertex.addOutgoingEdge(forwardEdge);
            toVertex.addInboundEdge(forwardEdge);
            toVertex.addOutgoingEdge(backwardEdge);

            edges.add(forwardEdge);
            forwardEdges.add(forwardEdge);
            edges.add(backwardEdge);
        }

        void addEdge(int from, int to, int capacity) {
            addEdge(from, to, 0, capacity);
        }

        void addFlow(int id, int flow) {
            /* To get a backward edge for a true forward edge (i.e id is even), we should get id + 1
             * due to the described above scheme. On the other hand, when we have to get a "backward"
             * edge for a backward edge (i.e. get a forward edge for backward - id is odd), id - 1
             * should be taken.
             *
             * It turns out that id ^ 1 works for both cases. Think this through! */
            edges.get(id).flow += flow;
            edges.get(id ^ 1).flow -= flow;
        }

        Vertex getSource() {
            return source;
        }

        void setSource(Vertex source) {
            this.source = source;
            vertices.add(source);
        }

        Vertex getTarget() {
            return target;
        }

        void setTarget(Vertex target) {
            this.target = target;
            vertices.add(target);
        }

        List<Vertex> getVertices() {
            return vertices;
        }

        List<Edge> getEdges() {
            return edges;
        }

        List<Edge> getInitialEdges() {
            List<Edge> result = new ArrayList<>();
            for (Edge edge : forwardEdges) {
                result.add(edge);
                if (result.size() == m) {
                    break;
                }
            }
            return result;
        }
    }

    /**
     * Vertex in {@link FlowNetworkGraph}.
     */
    static class Vertex {
        private final int id;
        private final List<Edge> inboundEdges;
        private final List<Edge> outgoingEdges;

        public Vertex(int id) {
            this.id = id;
            inboundEdges = new ArrayList<>();
            outgoingEdges = new ArrayList<>();
        }

        void addInboundEdge(Edge edge) {
            this.inboundEdges.add(edge);
        }

        void addOutgoingEdge(Edge edge) {
            this.outgoingEdges.add(edge);
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
    }

    /**
     * Edge in {@link FlowNetworkGraph}.
     */
    static class Edge {
        private final int id;
        private final Vertex from;
        private final Vertex to;
        private final int lowerBound;
        private int capacity;
        private int flow;

        Edge(int id, Vertex from, Vertex to, int lowerBound, int capacity) {
            this.id = id;
            this.from = from;
            this.to = to;
            this.lowerBound = lowerBound;
            this.capacity = capacity;
        }

        int getId() {
            return id;
        }

        Vertex getFrom() {
            return from;
        }

        Vertex getTo() {
            return to;
        }

        int getLowerBound() {
            return lowerBound;
        }

        int getCapacity() {
            return capacity;
        }

        int getFlow() {
            return flow;
        }
    }

    /**
     * Class represents solution of circulation problem.
     */
    static class CirculationResponse {
        private final boolean exist;
        private final List<Edge> edges;

        // Constructor for response with no circulation.
        CirculationResponse() {
            exist = false;
            edges = Collections.emptyList();
        }

        CirculationResponse(List<Edge> edges) {
            this.exist = true;
            this.edges = edges;
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            if (exist) {
                stringBuilder.append("YES");
                stringBuilder.append("\n");
                for (Edge edge : edges) {
                    stringBuilder.append(edge.getFlow());
                    stringBuilder.append("\n");
                }
            } else {
                stringBuilder.append("NO");
            }
            return stringBuilder.toString();
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
