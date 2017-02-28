package week2;

import org.junit.Test;
import util.Asserts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertTrue;
import static util.CommonTestHelper.generateGenome;
import static util.CommonTestHelper.generateKmer;

/**
 * Unit tests for {@link PhiX174kMerComposition}.
 */
public class PhiX174kMerCompositionTest {
    PhiX174kMerComposition solver = new PhiX174kMerComposition();

    @Test
    public void assemblyGenome_10fixed() throws Exception {
        testAssemblyGenome("AAATTATAGG".toCharArray(), 5);
    }

    @Test
    public void assemblyGenome_10() throws Exception {
        testAssemblyGenome(10, 5);
    }

    @Test
    public void assemblyGenome_100() throws Exception {
        int genomeLength = 100;
        int kmerLength = 10;
        testAssemblyGenome(genomeLength, kmerLength);
    }

    @Test
    public void assemblyGenome_1000() throws Exception {
        int genomeLength = 1000;
        int kmerLength = 10;

        testAssemblyGenome(genomeLength, kmerLength);
    }

    @Test
    public void assemblyGenome_5396() throws Exception {
        int genomeLength = 5396;
        int kmerLength = 10;

        testAssemblyGenome(genomeLength, kmerLength);
    }

    private void testAssemblyGenome(int genomeLength, int kmerLength) {
        char[] genome = generateGenome(genomeLength);
        List<String> kmers = new ArrayList<>(genomeLength);
        for (int i = 0; i < genome.length; i++) {
            kmers.add(generateKmer(genome, kmerLength, i));
        }
        Collections.sort(kmers);

        String assembledGenome = solver.assemblyGenome(kmers);
        Asserts.assertGenomeEquals(String.valueOf(genome), assembledGenome);
    }

    private void testAssemblyGenome(char[] genome, int kmerLength) {
        List<String> kmers = new ArrayList<>(genome.length);
        for (int i = 0; i < genome.length; i++) {
            kmers.add(generateKmer(genome, kmerLength, i));
        }

        String assembledGenome = solver.assemblyGenome(kmers);
        Asserts.assertGenomeEquals(String.valueOf(genome), assembledGenome);
    }

}