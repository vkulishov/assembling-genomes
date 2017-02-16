package week2;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Solution for problem 1 of week 2.
 * Puzzle Assembly
 * <p>
 * Task. Let each square piece be defined by the four colors of its four edges, in the format (up,
 * left,down,right). Let a “valid placement” be defined as a placement of 𝑛^2 square pieces onto an
 * 𝑛-by-𝑛 grid such that all “outer edges” (i.e., edges that border no other square pieces), and
 * only these edges, are black, and for all edges that touch an edge in another square piece, the
 * two touching edges are the same color.
 * <p>
 * Dataset. Each line of the input contains a single square piece, in the format described above:
 * (up,left,down,right). You will be given 25 such pieces in total (so 25 lines of input). Note that
 * all “outer edges” (i.e., edges that border no other square pieces on the puzzle) are black, and none of
 * the “inner edges” (i.e., edges not on the outside border of the puzzle) are black.
 * <p>
 * Output. Output a “valid placement" of the inputted pieces in a 5-by-5 grid. Specifically, your
 * output should be exactly 5 lines long (representing the 5 rows of the grid), and on each line of your output,
 * you should output 5 square pieces in the exact format described, (up,left,down,right), separated
 * by semicolons. There should be no space characters at all in your output.
 */
public class PuzzleAssembly {
    private static final int DEFAULT_GRID_SIZE = 5;
    private static final String BORDER_COLOR = "black";
    private static final int CELL_UNSET_VALUE = -1;

    private final int gridSize;

    PuzzleAssembly(int gridSize) {
        this.gridSize = gridSize;
    }

    public static void main(String... args) {
        Scanner stdin = new Scanner(new BufferedInputStream(System.in));
        Map<Integer, Piece> pieces = new HashMap<>();
        int readsCounter = 0;

        while (readsCounter < DEFAULT_GRID_SIZE * DEFAULT_GRID_SIZE && stdin.hasNext()) {
            pieces.put(readsCounter, new Piece(readsCounter, stdin.nextLine()));
            readsCounter++;
        }

        PuzzleAssembly solver = new PuzzleAssembly(DEFAULT_GRID_SIZE);
        int[] solutionArray = solver.solvePuzzle(pieces.values());
        StringBuilder answer = new StringBuilder();
        for (int i = 0; i < solutionArray.length; ) {
            for (int j = 0; j < solver.gridSize; j++) {
                if (j != 0) {
                    answer.append(";");
                }
                Piece piece = pieces.get(solutionArray[i++]);
                answer.append(piece.toString());
            }
            if (i < solutionArray.length - 1) {
                answer.append("\n");
            }
        }
        System.out.println(answer);
    }

    /**
     * Solve the assembly puzzle problem using the provided set of {@link Piece}.
     *
     * @param pieces
     * @return array presentation of a solution grid. every cell in the arrays contains index of piece that it holds
     */
    public int[] solvePuzzle(Collection<Piece> pieces) {
        PiecesSearchIndex searchIndex = PiecesSearchIndex.build(pieces);
        int solutionSize = this.gridSize * this.gridSize;
        boolean[] usedPieces = new boolean[solutionSize];
        int[] solution = new int[solutionSize];
        Arrays.fill(solution, CELL_UNSET_VALUE);

        // Set unique pieces to their correct positions.
        Integer leftUpperCorner = searchIndex.findByUpLeft(BORDER_COLOR, BORDER_COLOR).get(0);
        solution[0] = leftUpperCorner;
        usedPieces[leftUpperCorner] = true;

        Integer lowerLeftCorner = searchIndex.findByDownLeft(BORDER_COLOR, BORDER_COLOR).get(0);
        solution[this.gridSize * this.gridSize - this.gridSize] = lowerLeftCorner;
        usedPieces[lowerLeftCorner] = true;

        Integer upperRightCorner = searchIndex.findByUpRight(BORDER_COLOR, BORDER_COLOR).get(0);
        solution[this.gridSize - 1] = upperRightCorner;
        usedPieces[upperRightCorner] = true;

        Integer lowerRightCorner = searchIndex.findByDownRight(BORDER_COLOR, BORDER_COLOR).get(0);
        solution[solutionSize - 1] = lowerRightCorner;
        usedPieces[lowerRightCorner] = true;

        doSolvePuzzle(solution, searchIndex, usedPieces);

        return solution;
    }

    /**
     * Checks whether all position in the given solution are set.
     */
    boolean isCompleteSolution(int[] solution) {
        for (int cell : solution) {
            if (cell == CELL_UNSET_VALUE) {
                return false;
            }
        }
        return true;
    }

    /**
     * Find forst position in a provided solution that has value {@code CELL_UNSET_VALUE}.
     */
    int findIncompletePosition(int[] solution) {
        for (int i = 1; i < solution.length - 1; i++) {
            if (solution[i] == CELL_UNSET_VALUE) {
                return i;
            }
        }
        throw new IllegalArgumentException("Completed solution provided");
    }

    /**
     * Recursively find solution for the provided params.
     *
     * @param solution    arrays presentation of the grid
     * @param searchIndex instance of {@link PiecesSearchIndex corresponding to the provided pieces
     * @param usedPieces  arrays of flags indicating if the piece with a corresponding index
     *                    has been used in the current solution.
     * @return true if solution is complete, false - otherwise
     */
    private boolean doSolvePuzzle(int[] solution, PiecesSearchIndex searchIndex, boolean[] usedPieces) {
        if (isCompleteSolution(solution)) {
            return true;
        }
        int nextPosition = findIncompletePosition(solution);
        List<Integer> availablePieces = null;
        if (nextPosition < this.gridSize) {
            // First row in a grid.
            Piece leftPiece = searchIndex.getByIndex(solution[nextPosition - 1]);
            availablePieces = filterUsedPieces(searchIndex.findByUpLeft(BORDER_COLOR, leftPiece.getRight()), usedPieces);
        } else if (nextPosition > this.gridSize * this.gridSize - this.gridSize) {
            // Last row in a grid
            Piece leftPiece = searchIndex.getByIndex(solution[nextPosition - 1]);
            Piece upPiece = searchIndex.getByIndex(solution[nextPosition - this.gridSize]);
            availablePieces = filterUsedPieces(searchIndex.findByUpLeftDown(
                    upPiece.getDown(), leftPiece.getRight(), BORDER_COLOR), usedPieces);
        } else {
            // Middle rows
            Piece upPiece = searchIndex.getByIndex(solution[nextPosition - this.gridSize]);
            if (nextPosition % this.gridSize == 0) {
                // First column
                availablePieces = filterUsedPieces(searchIndex.findByUpLeft(upPiece.getDown(), BORDER_COLOR), usedPieces);
            } else if (nextPosition % this.gridSize == this.gridSize - 1) {
                // Last column
                availablePieces = filterUsedPieces(searchIndex.findByUpRight(upPiece.getDown(), BORDER_COLOR), usedPieces);
            } else {
                // middle columns
                Piece leftPiece = searchIndex.getByIndex(solution[nextPosition - 1]);
                availablePieces = filterUsedPieces(searchIndex.findByUpLeft(upPiece.getDown(), leftPiece.getRight()), usedPieces);
            }
        }

        for (int pieceIndex : availablePieces) {
            solution[nextPosition] = pieceIndex;
            usedPieces[pieceIndex] = true;
            if (doSolvePuzzle(solution, searchIndex, usedPieces)) {
                return true;
            }
            usedPieces[pieceIndex] = false;
        }
        solution[nextPosition] = CELL_UNSET_VALUE;

        return false;
    }

    private List<Integer> filterUsedPieces(List<Integer> availablePieces, boolean[] usedPieces) {
        if (availablePieces == null) {
            return Collections.emptyList();
        }
        List<Integer> result = new ArrayList<>();
        for (int pieceIndex : availablePieces) {
            if (!usedPieces[pieceIndex]) {
                result.add(pieceIndex);
            }
        }
        return result;
    }

    /**
     * Class representing piece of puzzle.
     */
    static class Piece {
        private final int index;
        private final String up;
        private final String left;
        private final String down;
        private final String right;

        public Piece(int index, String pieceString) {
            this.index = index;
            int openBracePosition = pieceString.indexOf('(');
            int closeBracePosition = pieceString.indexOf(')');
            String[] pieceSides = pieceString.substring(openBracePosition + 1, closeBracePosition).split(",");
            this.up = pieceSides[0];
            this.left = pieceSides[1];
            this.down = pieceSides[2];
            this.right = pieceSides[3];
        }

        public String getUp() {
            return up;
        }

        public String getLeft() {
            return left;
        }

        public String getDown() {
            return down;
        }

        public String getRight() {
            return right;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public String toString() {
            return String.format("(%s,%s,%s,%s)", up, left, down, right);
        }
    }

    /**
     * Helper class that provides methods for searching puzzle pieces by side colors.
     */
    static class PiecesSearchIndex {
        private final Map<Integer, Piece> indexedPieces;
        private final Map<String, List<Integer>> upLeftIndexed;
        private final Map<String, List<Integer>> upRightIndexed;
        private final Map<String, List<Integer>> downLeftIndexed;
        private final Map<String, List<Integer>> downRightIndexed;
        private final Map<String, List<Integer>> upLeftDownIndexed;

        private PiecesSearchIndex(
                Map<Integer, Piece> indexedPieces,
                Map<String, List<Integer>> upLeftIndexed,
                Map<String, List<Integer>> upRightIndexed,
                Map<String, List<Integer>> downLeftIndexed,
                Map<String, List<Integer>> downRightIndexed,
                Map<String, List<Integer>> upLeftDownIndexed) {
            this.indexedPieces = indexedPieces;
            this.upLeftIndexed = upLeftIndexed;
            this.upRightIndexed = upRightIndexed;
            this.downLeftIndexed = downLeftIndexed;
            this.downRightIndexed = downRightIndexed;
            this.upLeftDownIndexed = upLeftDownIndexed;
        }

        public static PiecesSearchIndex build(Collection<Piece> pieces) {
            Map<Integer, Piece> indexedPieces = new HashMap<>(pieces.size());
            Map<String, List<Integer>> upLeftIndexed = new HashMap<>();
            Map<String, List<Integer>> upRightIndexed = new HashMap<>();
            Map<String, List<Integer>> downLeftIndexed = new HashMap<>();
            Map<String, List<Integer>> downRightIndexed = new HashMap<>();
            Map<String, List<Integer>> upLeftDownIndexed = new HashMap<>();

            for (Piece piece : pieces) {
                indexedPieces.put(piece.getIndex(), piece);
                addIndex(getKey(piece.getUp(), piece.getLeft()), piece.getIndex(), upLeftIndexed);
                addIndex(getKey(piece.getUp(), piece.getRight()), piece.getIndex(), upRightIndexed);
                addIndex(getKey(piece.getDown(), piece.getLeft()), piece.getIndex(), downLeftIndexed);
                addIndex(getKey(piece.getDown(), piece.getRight()), piece.getIndex(), downRightIndexed);
                addIndex(getKey(piece.getUp(), piece.getLeft(), piece.getDown()), piece.getIndex(), upLeftDownIndexed);
            }

            return new PiecesSearchIndex(
                    indexedPieces,
                    upLeftIndexed,
                    upRightIndexed,
                    downLeftIndexed,
                    downRightIndexed,
                    upLeftDownIndexed);
        }

        private static void addIndex(
                String key,
                int pieceIndex,
                Map<String, List<Integer>> destination) {
            List<Integer> indexes = destination.get(key);
            if (indexes == null) {
                indexes = new ArrayList<>();
                destination.put(key, indexes);
            }
            indexes.add(pieceIndex);
        }

        private static String getKey(String... parts) {
            return String.join("-", parts);
        }

        public Piece getByIndex(int index) {
            return indexedPieces.get(index);
        }

        public List<Integer> findByUpLeft(String up, String left) {
            return upLeftIndexed.get(getKey(up, left));
        }

        public List<Integer> findByUpLeftDown(String up, String left, String down) {
            return upLeftDownIndexed.get(getKey(up, left, down));
        }

        public List<Integer> findByUpRight(String up, String right) {
            return upRightIndexed.get(getKey(up, right));
        }

        public List<Integer> findByDownLeft(String down, String left) {
            return downLeftIndexed.get(getKey(down, left));
        }

        public List<Integer> findByDownRight(String down, String right) {
            return downRightIndexed.get(getKey(down, right));
        }
    }
}
