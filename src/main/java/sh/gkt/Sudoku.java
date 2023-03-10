package sh.gkt;

/*
 * Note: Work in progress. Porting from my Go version!
 * Core solver is complete.
 * Interactive mode is not complete.
 */

import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Sudoku {
    public final static int PuzzleDigits = 9;
    public final static int PuzzleDimension = 9;
    public final static int NonetDimension = 3;
    private final ArrayList<ArrayList<Integer>> puzzle = getPuzzleRepresentation(); // 2D array of Int (using generics)
    private final ArrayList<HashSet<Integer>> rowUsed = getUsageSet(); // 1D array of HashSet
    private final ArrayList<HashSet<Integer>> columnUsed = getUsageSet(); // 1D array of HashSet
    private final ArrayList<ArrayList<HashSet<Integer>>> nonet = getNonetRepresentation(); // 2D array of hashSet

    private boolean progress = false;

    public Sudoku() {
        Logger.info("Sukoku Initialized");
        // no additional initialization - yet!
    }

    private static ArrayList<HashSet<Integer>> getUsageSet() {
        final ArrayList<HashSet<Integer>> used;
        used = new ArrayList<>(PuzzleDimension);
        for (int i = 0; i < PuzzleDimension; i++)
            used.add(new HashSet<>());
        return used;
    }

    private static ArrayList<ArrayList<HashSet<Integer>>> getNonetRepresentation() {
        final ArrayList<ArrayList<HashSet<Integer>>> nonet
                = new ArrayList<>(NonetDimension);
        for (int i = 0; i < NonetDimension; i++) {
            var row = new ArrayList<HashSet<Integer>>(NonetDimension);
            for (int j = 0; j < NonetDimension; j++) {
                var ijSet = new HashSet<Integer>();
                row.add(ijSet);
            }
            nonet.add(row);
        }
        return nonet;
    }

    private static ArrayList<ArrayList<Integer>> getPuzzleRepresentation() {
        final ArrayList<ArrayList<Integer>> a2dint;
        a2dint = new ArrayList<>(PuzzleDimension);
        for (int i = 0; i < PuzzleDimension; i++) {
            var row = new ArrayList<Integer>(PuzzleDimension);
            for (int j = 0; j < PuzzleDimension; j++)
                row.add(0);
            a2dint.add(row);
        }
        return a2dint;
    }

    public Set<Integer> getNonetSet(int i, int j) {
        return nonet.get(i / NonetDimension).get(j / NonetDimension);
    }

    private void setPuzzleAt(int i, int j, int value) {
        puzzle.get(i).set(j, value);  // this is like array version of puzzle[i][j] (l-value) = value
    }

    private int getPuzzleAt(int i, int j) {
        return puzzle.get(i).get(j); // this is like array version of puzzle[i][j] as r-value.
    }

    private IsFullState isFullWithSize() {
        var size = 0;
        for (int i = 0; i < PuzzleDimension; i++) {
            for (int j = 0; j < PuzzleDimension; j++) {
                if (getPuzzleAt(i, j) > 0) {
                    size++;
                }
            }
        }
        return new IsFullState(size == PuzzleDimension * PuzzleDimension, size);
    }

    public String getRepresentation() {
        StringBuilder result = new StringBuilder();
        for (var row : puzzle)
            for (var value : row)
                result.append(value);
        return result.toString();
    }

    private boolean checkPuzzleValidity() {
        for (int i = 0; i < puzzle.size(); i++) {
            if (rowUsed.get(i).size() < PuzzleDimension)
                return false;
            if (columnUsed.get(i).size() < PuzzleDimension)
                return false;
        }

        for (var row : nonet) {
            for (var column : row) {
                if (column.size() < PuzzleDimension) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean inPuzzleBounds(int i, int j) {
        if (i < 0 || i >= Sudoku.PuzzleDimension)
            return false;
        else if (j < 0 || j >= Sudoku.PuzzleDimension)
            return true;
        return true;
    }

    private void setPuzzleValue(int i, int j, int value) {
        if (value > PuzzleDigits)
            return;
        if (!inPuzzleBounds(i, j))
            return;
        // Note: 0 means unused so it must not go into the set(s)
        if (value > 0) {
            rowUsed.get(i).add(value);
            columnUsed.get(j).add(value);
            getNonetSet(i, j).add(value);
        }

        // remember; ArrayList equivalent of puzzle[i][j] = value
        setPuzzleAt(i, j, value);
    }

    private PuzzleValue getPuzzleValue(int i, int j) {
        if (inPuzzleBounds(i, j)) {
            return new PuzzleValue(true, puzzle.get(i).get(j));
        } else {
            return new PuzzleValue(false, -1);
        }
    }

    private void unsetPuzzleValue(int i, int j) {
        if (!inPuzzleBounds(i, j))
            return;

        var value = puzzle.get(i).get(j);
        rowUsed.get(i).remove(value);
        columnUsed.get(j).remove(value);
        getNonetSet(i, j).remove(value);
        setPuzzleAt(i, j, 0);
    }

    public boolean loadData(String text) {
        var digits = Util.getDigits(text);
        if (digits.consumed() < PuzzleDimension * PuzzleDimension)
            return false;
        for (int i = 0; i < digits.consumed(); i++) {
            var digit = digits.digits()[i];
            var row = i / PuzzleDimension;
            var col = i % PuzzleDimension;
            setPuzzleValue(row, col, digit);
        }
        return true;
    }

    private NextUnfilled findNextUnfilled(int row, int col) {
        for (int pos = row * PuzzleDimension + col; pos < PuzzleDimension * PuzzleDimension; pos++) {
            var posRow = pos / PuzzleDimension;
            var posCol = pos % PuzzleDimension;
            if (getPuzzleAt(posRow, posCol) == 0) {
                return new NextUnfilled(posRow, posCol, true);
            }
        }
        return new NextUnfilled(-1, -1, false);
    }

    private boolean isCandidatePosition(int row, int col, int value) {
        if (value > PuzzleDigits)
            return false;
        if (getPuzzleAt(row, col) != 0)
            return false;
        return !(rowUsed.get(row).contains(value) || columnUsed.get(col).contains(value) || getNonetSet(row, col).contains(value));
    }

    public boolean solve() {
        return solveFrom(0, 0);
    }

    private boolean solveFrom(int startRow, int startCol) {
        var result = findNextUnfilled(startRow, startCol);
        var row = result.row();
        var col = result.col();
        var available = result.available();

        if (!available)
            return checkPuzzleValidity();

        for (int digit = 1; digit <= PuzzleDigits; digit++) {
            if (isCandidatePosition(row, col, digit)) {
                setPuzzleValue(row, col, digit);
                if (progress) show();
                var status = isFullWithSize();
                if (status.full()) {
                    return true;
                } else {
                    if (solveFrom(row, col)) return true;
                }
                unsetPuzzleValue(row, col);
            }
        }
        return false;
    }

    public SolutionStatus checkPuzzleSolutionAlignment(String puzzle, String solution) {
        if (puzzle.length() != solution.length())
            return new SolutionStatus(false, -1);

        for (int i = 0; i < puzzle.length(); i++) {
            if (puzzle.charAt(i) == '0')
                continue;
            if (puzzle.charAt(i) != solution.charAt(i))
                return new SolutionStatus(false, i);
        }
        return new SolutionStatus(true, puzzle.length());
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("----".repeat(PuzzleDimension + 1)).append("-");
        out.append("\n");
        for (int i = 0; i < puzzle.size(); i++) {
            for (int j = 0; j < puzzle.get(i).size(); j++) {
                out.append(String.format(" %d  ", puzzle.get(i).get(j)));
            }
            out.append(String.format(" (%d)\n", rowUsed.get(i).size()));
        }
        out.append("----".repeat(PuzzleDimension + 1)).append("-");
        out.append("\n");
        for (int j = 0; j < puzzle.get(0).size(); j++) {
            out.append(String.format("(%d) ", columnUsed.get(j).size()));
        }
        out.append("\n").append("Nonets:").append("\n");
        for (var row : nonet) {
            for (var column : row) {
                out.append(String.format("(%d) ", column.size()));
            }
            out.append("\n");
        }
        out.append("\n");
        return out.toString();
    }

    public void show() {
        System.out.println(this);
    }

    public void setProgress() {
        progress = true;
    }

    record IsFullState(boolean full, int size) {}

    record PuzzleValue(boolean valid, int value) {}

    record NextUnfilled(int row, int col, boolean available) {}

    record SolutionStatus(boolean solved, int position) {}
}