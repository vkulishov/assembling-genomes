package week2;

import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link PuzzleAssembly}.
 */
public class PuzzleAssemblyTest {
    private static final String LEFT_TOP_CORNER_PIECE = "(black,black,red,red)";
    private static final String LEFT_BOTTOM_CORNER_PIECE = "(yellow,black,black,blue)";
    private static final String RIGHT_TOP_CORNER_PIECE = "(black,purple,green,black)";
    private static final String RIGHT_BOTTOM_CORNER_PIECE = "(orange,yellow,black,black)";
    private static final String[] EXAMPLE_GRID = new String[]{
            "(yellow,black,black,blue)",
            "(blue,blue,black,yellow)",
            "(orange,yellow,black,black)",
            "(red,black,yellow,green)",
            "(orange,green,blue,blue)",
            "(green,blue,orange,black)",
            "(black,black,red,red)",
            "(black,red,orange,purple)",
            "(black,purple,green,black)"
    };

    @Test
    public void testPieceConstructor() {
        PuzzleAssembly.Piece piece = new PuzzleAssembly.Piece(1, "(black,white,green,blue)");

        assertEquals("black", piece.getUp());
        assertEquals("white", piece.getLeft());
        assertEquals("green", piece.getDown());
        assertEquals("blue", piece.getRight());
        assertEquals(1, piece.getIndex());
    }

    @Test
    public void testPiecesSearchIndex_upLeft() {
        Set<PuzzleAssembly.Piece> pieces = new HashSet<>();
        pieces.add(new PuzzleAssembly.Piece(1, LEFT_TOP_CORNER_PIECE));
        pieces.add(new PuzzleAssembly.Piece(2, LEFT_BOTTOM_CORNER_PIECE));
        pieces.add(new PuzzleAssembly.Piece(3, RIGHT_TOP_CORNER_PIECE));
        pieces.add(new PuzzleAssembly.Piece(4, RIGHT_BOTTOM_CORNER_PIECE));

        PuzzleAssembly.PiecesSearchIndex searchIndex = PuzzleAssembly.PiecesSearchIndex.build(pieces);
        List<Integer> result1 = searchIndex.findByUpLeft("black", "black");
        List<Integer> result2 = searchIndex.findByUpLeft("yellow", "black");
        List<Integer> result3 = searchIndex.findByUpLeft("black", "purple");
        List<Integer> result4 = searchIndex.findByUpLeft("orange", "yellow");

        assertEquals(1, (int) result1.get(0));
        assertEquals(2, (int) result2.get(0));
        assertEquals(3, (int) result3.get(0));
        assertEquals(4, (int) result4.get(0));
    }

    @Test
    public void testPiecesSearchIndex_upRight() {
        Set<PuzzleAssembly.Piece> pieces = new HashSet<>();
        pieces.add(new PuzzleAssembly.Piece(1, LEFT_TOP_CORNER_PIECE));
        pieces.add(new PuzzleAssembly.Piece(2, LEFT_BOTTOM_CORNER_PIECE));
        pieces.add(new PuzzleAssembly.Piece(3, RIGHT_TOP_CORNER_PIECE));
        pieces.add(new PuzzleAssembly.Piece(4, RIGHT_BOTTOM_CORNER_PIECE));

        PuzzleAssembly.PiecesSearchIndex searchIndex = PuzzleAssembly.PiecesSearchIndex.build(pieces);
        List<Integer> result1 = searchIndex.findByUpRight("black", "red");
        List<Integer> result2 = searchIndex.findByUpRight("yellow", "blue");
        List<Integer> result3 = searchIndex.findByUpRight("black", "black");
        List<Integer> result4 = searchIndex.findByUpRight("orange", "black");

        assertEquals(1, (int) result1.get(0));
        assertEquals(2, (int) result2.get(0));
        assertEquals(3, (int) result3.get(0));
        assertEquals(4, (int) result4.get(0));
    }

    @Test
    public void testPiecesSearchIndex_downLeft() {
        Set<PuzzleAssembly.Piece> pieces = new HashSet<>();
        pieces.add(new PuzzleAssembly.Piece(1, LEFT_TOP_CORNER_PIECE));
        pieces.add(new PuzzleAssembly.Piece(2, LEFT_BOTTOM_CORNER_PIECE));
        pieces.add(new PuzzleAssembly.Piece(3, RIGHT_TOP_CORNER_PIECE));
        pieces.add(new PuzzleAssembly.Piece(4, RIGHT_BOTTOM_CORNER_PIECE));

        PuzzleAssembly.PiecesSearchIndex searchIndex = PuzzleAssembly.PiecesSearchIndex.build(pieces);
        List<Integer> result1 = searchIndex.findByDownLeft("red", "black");
        List<Integer> result2 = searchIndex.findByDownLeft("black", "black");
        List<Integer> result3 = searchIndex.findByDownLeft("green", "purple");
        List<Integer> result4 = searchIndex.findByDownLeft("black", "yellow");

        assertEquals(1, (int) result1.get(0));
        assertEquals(2, (int) result2.get(0));
        assertEquals(3, (int) result3.get(0));
        assertEquals(4, (int) result4.get(0));
    }

    @Test
    public void testPiecesSearchIndex_downRight() {
        Set<PuzzleAssembly.Piece> pieces = new HashSet<>();
        pieces.add(new PuzzleAssembly.Piece(1, LEFT_TOP_CORNER_PIECE));
        pieces.add(new PuzzleAssembly.Piece(2, LEFT_BOTTOM_CORNER_PIECE));
        pieces.add(new PuzzleAssembly.Piece(3, RIGHT_TOP_CORNER_PIECE));
        pieces.add(new PuzzleAssembly.Piece(4, RIGHT_BOTTOM_CORNER_PIECE));

        PuzzleAssembly.PiecesSearchIndex searchIndex = PuzzleAssembly.PiecesSearchIndex.build(pieces);
        List<Integer> result1 = searchIndex.findByDownRight("red", "red");
        List<Integer> result2 = searchIndex.findByDownRight("black", "blue");
        List<Integer> result3 = searchIndex.findByDownRight("green", "black");
        List<Integer> result4 = searchIndex.findByDownRight("black", "black");

        assertEquals(1, (int) result1.get(0));
        assertEquals(2, (int) result2.get(0));
        assertEquals(3, (int) result3.get(0));
        assertEquals(4, (int) result4.get(0));
    }

    @Test
    public void testPiecesSearchIndex_upLeftDown() {
        Set<PuzzleAssembly.Piece> pieces = new HashSet<>();
        pieces.add(new PuzzleAssembly.Piece(1, LEFT_TOP_CORNER_PIECE));
        pieces.add(new PuzzleAssembly.Piece(2, LEFT_BOTTOM_CORNER_PIECE));
        pieces.add(new PuzzleAssembly.Piece(3, RIGHT_TOP_CORNER_PIECE));
        pieces.add(new PuzzleAssembly.Piece(4, RIGHT_BOTTOM_CORNER_PIECE));

        PuzzleAssembly.PiecesSearchIndex searchIndex = PuzzleAssembly.PiecesSearchIndex.build(pieces);
        List<Integer> result1 = searchIndex.findByUpLeftDown("black", "black", "red");
        List<Integer> result2 = searchIndex.findByUpLeftDown("yellow", "black", "black");
        List<Integer> result3 = searchIndex.findByUpLeftDown("black", "purple", "green");
        List<Integer> result4 = searchIndex.findByUpLeftDown("orange", "yellow", "black");

        assertEquals(1, (int) result1.get(0));
        assertEquals(2, (int) result2.get(0));
        assertEquals(3, (int) result3.get(0));
        assertEquals(4, (int) result4.get(0));
    }

    @Test
    public void testSolvePuzzle() {
        Set<PuzzleAssembly.Piece> pieces = new HashSet<>(EXAMPLE_GRID.length);
        int readsCounter = 0;
        for (String read : EXAMPLE_GRID) {
            pieces.add(new PuzzleAssembly.Piece(readsCounter++, read));
        }

        PuzzleAssembly solver = new PuzzleAssembly(3);
        int[] positions = solver.solvePuzzle(pieces);

        assertArrayEquals(new int[]{6, 7, 8, 3, 4, 5, 0, 1, 2}, positions);
    }
}