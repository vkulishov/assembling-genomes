package util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Utility class providing common helper methods for unit tests of the assembly genome project.
 */
public class CommonTestHelper {
    private static final char[] NUKLEOTIDES = new char[]{'A', 'C', 'G', 'T'};
    private static final Random rnd = new Random();

    public static char[] generateGenome(int length) {
        char[] genomeChars = new char[length];
        for (int i = 0; i < length; i++) {
            genomeChars[i] = NUKLEOTIDES[rnd.nextInt(NUKLEOTIDES.length)];
        }
        return genomeChars;
    }

    public static String generateKmer(char[] genome, int kmerLength, int startPosition) {
        char[] readChars = new char[kmerLength];

        char[] subRead = Arrays.copyOfRange(genome, startPosition, Math.min(startPosition +
                kmerLength, genome.length));
        System.arraycopy(subRead, 0, readChars, 0, subRead.length);

        if (subRead.length < kmerLength) {
            System.arraycopy(genome, 0, readChars, subRead.length, kmerLength - subRead.length);
        }

        return String.valueOf(readChars);
    }

    public static Set<String> buildReads(String... reads) {
        return new HashSet<>(Arrays.asList(reads));
    }
}
