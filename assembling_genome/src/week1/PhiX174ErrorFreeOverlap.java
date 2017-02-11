package week1;

import java.io.BufferedInputStream;
import java.util.*;

/**
 * Solution of problem 1 from week 1.
 * Assembling the phi X174 Genome from Error-Free Reads Using Overlap Graphs
 * <p>
 * Task: Given a list of error-free reads, perform the task of Genome Assembly and return the
 * circular genome from which they came.
 * </p>
 * <p>
 * Output. Output the assembled genome on a single line.
 * </p>
 * <p>
 * Note: the whole task is solved within single file due to specific of grader.
 * </p>
 */
public class PhiX174ErrorFreeOverlap {
    private static final int DEFAULT_READS_NUMBER = 1618;
    private static final int DEFAULT_MIN_OVERLAP_LENGTH = 13;
    private static final Vertex START_VERTEX = new Vertex(0, 0);

    private final int minOverlapLength;

    PhiX174ErrorFreeOverlap(int minOverlapLength) {
        this.minOverlapLength = minOverlapLength;
    }

    public static void main(String... args) {
        // Read test data.
        Scanner stdin = new Scanner(new BufferedInputStream(System.in));
        Set<String> reads = new HashSet<>();
        int readsCounter = 0;
        while (readsCounter++ < DEFAULT_READS_NUMBER && stdin.hasNext()) {
            reads.add(stdin.nextLine());
        }

        // Solve the problem.
        PhiX174ErrorFreeOverlap instance =
                new PhiX174ErrorFreeOverlap(DEFAULT_MIN_OVERLAP_LENGTH);
        String genome = instance.assemblyGenome(reads.toArray(new String[reads.size()]));

        // Provide solution to a grader.
        System.out.println(genome);
    }

    /**
     * Solves the problem of assembling a DNA sequence from the provided DNA fragments
     *
     * @param reads DNA fragments
     * @return DNA sequence
     */
    public String assemblyGenome(String[] reads) {
        Set<String> readsSet = new HashSet<>(Arrays.asList(reads));
        String[] readsWithoutDuplicates = readsSet.toArray(new String[readsSet.size()]);
        List<Vertex>[] adjacencyList = buildOverlapGraph(readsWithoutDuplicates);
        Deque<Vertex> hamiltonianPath = buildLongestHamiltonianPath(adjacencyList);

        return assemblyGenome(readsWithoutDuplicates, hamiltonianPath);
    }

    /**
     * Builds overlap graph in a form of adjacency list for the provided reads
     */
    List<Vertex>[] buildOverlapGraph(String[] reads) {
        @SuppressWarnings("unchecked")
        List<Vertex>[] adjacencyList = new List[reads.length];
        for (int i = 0; i < adjacencyList.length; i++) {
            adjacencyList[i] = new LinkedList<>();
        }

        for (int i = 0; i < reads.length - 1; i++) {
            for (int j = i + 1; j < reads.length; j++) {
                int stringsForwardOrderOverlap = stringsOverlap(reads[i], reads[j]);
                if (stringsForwardOrderOverlap >= this.minOverlapLength) {
                    adjacencyList[i].add(new Vertex(j, stringsForwardOrderOverlap));
                }
                int stringsReverseOrderOverlap = stringsOverlap(reads[j], reads[i]);
                if (stringsReverseOrderOverlap >= this.minOverlapLength) {
                    adjacencyList[j].add(new Vertex(i, stringsReverseOrderOverlap));
                }
            }
        }

        for (List<Vertex> list : adjacencyList) {
            Collections.sort(list);
        }

        return adjacencyList;
    }

    /**
     * Calculates an overlap of input strings. Overlap is a length of the string1 suffix that is
     * equal to the string2 prefix. Example: string1 = "AACGT", string2 = "GTCCA". Overlap value
     * is 2 ("GT").
     */
    int stringsOverlap(String string1, String string2) {
        int overlapLength = 0;
        int prefixPosition = 0;
        int suffixPosition = 0;

        while (prefixPosition < string1.length()) {
            int overlapPosition = prefixPosition;
            while (overlapPosition < string1.length()
                    && string1.charAt(overlapPosition) == string2.charAt(suffixPosition)) {
                overlapLength++;
                overlapPosition++;
                suffixPosition++;
            }
            if (overlapPosition == string1.length()) {
                break;
            }

            overlapLength = 0;
            suffixPosition = 0;
            prefixPosition++;
        }
        return overlapLength;
    }

    /**
     * Build Hamiltonian path for the given graph using greedy approach (on every step select
     * node with the maximum weight).
     *
     * @param adjacencyList graph
     * @return linked list of vertices that represent the Hamiltonian path for the given graph
     */
    Deque<Vertex> buildLongestHamiltonianPath(List<Vertex>[] adjacencyList) {
        boolean[] visitedVertices = new boolean[adjacencyList.length];
        Deque<Vertex> resultQueue = new LinkedList<>();
        addVertexToLongestHamiltonianPath(adjacencyList, resultQueue, visitedVertices,
                START_VERTEX);
        return resultQueue;
    }

    /**
     * Recursive method for adding another vertex to a Hamiltonian path for the provide graph.
     */
    boolean addVertexToLongestHamiltonianPath(List<Vertex>[] adjacencyList, Deque<Vertex> path,
                                              boolean[] visited, Vertex vertex) {
        path.add(vertex);
        visited[vertex.index] = true;

        if (path.size() == adjacencyList.length) {
            return true;
        }
        for (Vertex v : adjacencyList[vertex.index]) {
            if (!visited[v.index] && addVertexToLongestHamiltonianPath(adjacencyList, path, visited, v)) {
                return true;
            }
        }
        Vertex last = path.removeLast();
        visited[last.index] = false;

        return false;
    }

    /**
     * Assembles genome from the provided reads and hamiltonian path of the overlap graph.
     * Note: it is assumed that the source genome is circular so the last vertex in the path may
     * have overlap with the start vertex, therefore the last element in the path should be
     * limited bounded by prefix and suffix (the rest elements are bound by prefix only).
     * Example:
     * first read = '[CA]TTTAC'
     * n-1 read = 'TGCST[GG]'
     * last read = '[GG]AAA[CA]'
     * overlap elements are wrapped into [].
     * The result genome will be:
     * CATTTAC..(overlap of n-2 reads)..TGCSTAAA
     */
    String assemblyGenome(String[] reads, Deque<Vertex> hamiltonianPath) {
        StringBuilder result = new StringBuilder();
        for (Vertex vertex : hamiltonianPath) {
            String read = reads[vertex.index];
            result.append(read.substring(vertex.weight));
        }
        int lastReadIndex = hamiltonianPath.getLast().index;
        int circularOverlap = stringsOverlap(reads[lastReadIndex], reads[0]);

        if (circularOverlap > 0) {
            result.delete(result.length() - circularOverlap, result.length());
        }
        return result.toString();
    }

    static class Vertex implements Comparable {
        int index;
        int weight;

        public Vertex(int index, int weight) {
            this.index = index;
            this.weight = weight;
        }

        @Override
        public int compareTo(Object o) {
            Vertex other = (Vertex) o;
            return Integer.compare(other.weight, this.weight);
        }
    }
}

