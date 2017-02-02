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
    private static final int READS_NUMBER = 3;
    private static final int MIN_OVERLAP_LENGTH = 3;

    public static void main(String... args) {
        // Read test data.
        Scanner stdin = new Scanner(new BufferedInputStream(System.in));
        Set<String> reads = new HashSet<>();
        int readsCounter = 0;
        while (readsCounter++ < READS_NUMBER && stdin.hasNext()) {
            reads.add(stdin.nextLine());
        }

        // Solve the problem.
        PhiX174ErrorFreeOverlap instance = new PhiX174ErrorFreeOverlap();
        String genome = instance.assemblyGenome(reads.toArray(new String[reads.size()]));

        // Provide solution to a grader.
        System.out.println(genome);
    }


    private String assemblyGenome(String[] reads) {
        List<Vertex>[] adjacencyList = buildOverlapGraph(reads);

        return adjacencyList.length + "";
    }

    private List<Vertex>[] buildOverlapGraph(String[] reads) {
        @SuppressWarnings("unchecked")
        List<Vertex>[] adjacencyList = new ArrayList[reads.length];
        for (int i = 0; i < adjacencyList.length; i++) {
            adjacencyList[i] = new ArrayList<>();
        }

        for (int i = 0; i < reads.length - 1; i++) {
            for (int j = i + 1; j < reads.length; j++) {
                int stringsForwardOrderOverlap = stringsOverlap(reads[i], reads[j]);
                int stringsReverseOrderOverlap = stringsOverlap(reads[j], reads[i]);
                if (Math.max(stringsForwardOrderOverlap, stringsReverseOrderOverlap) >= MIN_OVERLAP_LENGTH) {
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

        return adjacencyList;
    }

    private int stringsOverlap(String string1, String string2) {
        int overlapLength = 0;
        int prefixPosition = 0;
        int suffixPosition = 0;

        while (suffixPosition < string2.length()) {
            int overlapPosition = suffixPosition;
            while (overlapPosition < string2.length()
                    && string1.charAt(prefixPosition++) == string2.charAt(overlapPosition++)) {
                overlapLength++;
            }
            if (overlapPosition == string2.length()) {
                break;
            }

            overlapLength = 0;
            prefixPosition = 0;
            suffixPosition++;
        }
        return overlapLength;
    }

    private static class Vertex implements Comparable {
        int index;
        int cost;

        public Vertex(int index, int cost) {
            this.index = index;
            this.cost = cost;
        }

        @Override
        public int compareTo(Object o) {
            Vertex other = (Vertex) o;
            return Integer.compare(this.cost, other.cost);
        }
    }
}

