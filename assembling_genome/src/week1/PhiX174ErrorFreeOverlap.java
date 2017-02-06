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
    private static final int DEFAULT_MIN_OVERLAP_LENGTH = 12;
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

    public String assemblyGenome(String[] reads) {
        List<Vertex>[] adjacencyList = buildOverlapGraph(reads);
        Deque<Vertex> hamiltonianPath = buildLongestHamiltonianPath(adjacencyList);

        return assemblyGenome(reads, hamiltonianPath);
    }

    List<Vertex>[] buildOverlapGraph(String[] reads) {
        @SuppressWarnings("unchecked")
        List<Vertex>[] adjacencyList = new List[reads.length];
        for (int i = 0; i < adjacencyList.length; i++) {
            adjacencyList[i] = new LinkedList<>();
        }

        for (int i = 0; i < reads.length - 1; i++) {
            for (int j = i + 1; j < reads.length; j++) {
                int stringsForwardOrderOverlap = stringsOverlap(reads[i], reads[j]);
                int stringsReverseOrderOverlap = stringsOverlap(reads[j], reads[i]);
                if (Math.max(stringsForwardOrderOverlap, stringsReverseOrderOverlap) >= this.minOverlapLength) {
                    if (stringsForwardOrderOverlap > stringsReverseOrderOverlap) {
                        adjacencyList[i].add(new Vertex(j, stringsForwardOrderOverlap));
                    } else if (stringsForwardOrderOverlap < stringsReverseOrderOverlap) {
                        adjacencyList[j].add(new Vertex(i, stringsReverseOrderOverlap));
                    } else {
                        adjacencyList[i].add(new Vertex(j, stringsForwardOrderOverlap));
                        adjacencyList[j].add(new Vertex(i, stringsReverseOrderOverlap));
                    }
                }
            }
        }

        for (List<Vertex> list : adjacencyList) {
            Collections.sort(list);
        }

        return adjacencyList;
    }

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

    Deque<Vertex> buildLongestHamiltonianPath(List<Vertex>[] adjacencyList) {
        boolean[] visitedVertices = new boolean[adjacencyList.length];
        Deque<Vertex> resultQueue = new LinkedList<>(); // Actual Hamiltonian path
        Deque<Vertex> nodesStack = new LinkedList<>();
        nodesStack.push(START_VERTEX);

        while (!nodesStack.isEmpty()) {
            Vertex node = nodesStack.pop();
            visitedVertices[node.index] = true;
            resultQueue.add(node);

            boolean nextNodeFound = false;
            for (Vertex vertex : adjacencyList[node.index]) {
                if (!visitedVertices[vertex.index]) {
                    nextNodeFound = true;
                    nodesStack.push(vertex);
                }
            }
            if (!nextNodeFound && resultQueue.size() < adjacencyList.length) {
                resultQueue.removeLast();
                visitedVertices[node.index] = false;
            } else if (resultQueue.size() == adjacencyList.length) {
                break;
            }
        }
        return resultQueue;
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
        Iterator<Vertex> pathIterator = hamiltonianPath.iterator();
        while (pathIterator.hasNext()) {
            Vertex vertex = pathIterator.next();
            // Detect the last element in the path.
            String read = reads[vertex.index];
            if (!pathIterator.hasNext()) {
                int circularOverlap = stringsOverlap(read, reads[0]);
                result.append(read.substring(vertex.weight, read.length() - circularOverlap));
            } else {
                result.append(read.substring(vertex.weight));
            }
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
            return Integer.compare(this.weight, other.weight);
        }
    }
}

