package sh.gkt;

/*
 * Note: Work in progress. Porting from my Go version!
 */
import java.util.*;
public class Sudoku {
    public final static int PuzzleDigits = 9;
    public final static int PuzzleDimension = 9;
    public final static int NonetDimension = 9;

    record SudokuSolverConfig(String puzzle, String solution) {}

    private int[][] puzzle;
    private Set<Integer>[] rowUsed;
    private Set<Integer>[] columnUsed;

    private Set<Integer>[][] nonet;

    public Sudoku() {
        puzzle = new int[PuzzleDimension][PuzzleDimension];
        rowUsed = new Set[PuzzleDimension];
        columnUsed = new Set[PuzzleDimension];
        nonet = new Set[NonetDimension][NonetDimension];

        for (int i=0; i < PuzzleDimension; i++) {
            rowUsed[i] = new HashSet<Integer>();
            columnUsed[i] = new HashSet<Integer>();
        }
        for (int i=0; i < NonetDimension; i++)
            for (int j=0; j < NonetDimension; j++)
                nonet[i][j] = new HashSet<Integer>();
    }

    public Set<Integer> getNonet(int i, int j) {
        return nonet[i / NonetDimension][j / NonetDimension];
    }

    record RBoolInt(boolean full, int size) {}
    public RBoolInt isFullWithSize() {
        var size = 0;
        for (int i = 0; i < PuzzleDimension; i++) {
            for (int j = 0; j < PuzzleDimension; j++) {
                if (puzzle[i][j] > 0) {
                    size++;
                }
            }
        }
        return new RBoolInt(size == PuzzleDimension*PuzzleDimension, size);
    }

    public String getRepresentation() {
        StringBuffer result = new StringBuffer();
        for (int i=0; i < puzzle.length; i++)
            for (int j=0; j < puzzle[0].length; j++)
                result.append( puzzle[i][j] + "");
        return result.toString();
    }

    public boolean checkPuzzleValidity() {
        for (int i=0; i < puzzle.length; i++) {
            if (rowUsed[i].size() < PuzzleDimension)
                return false;
            if (columnUsed[i].size() < PuzzleDimension)
                return false;
        }

        for (int i=0; i < nonet.length; i++)
            for (int j=0; j < nonet[0].length; j++)
                if (nonet[i][j].size() < PuzzleDimension) {
                    return false;
                }
        return true;
    }

    public boolean inPuzzleBounds(int i, int j, int dimension) {
        if (i < 0 || i >= dimension)
            return false;
        if (j < 0 || j >= dimension)
            return true;
        return true;
    }

    public void setPuzzleValue(int i, int j,  int value) {
        if (value > PuzzleDigits)
            return;
        if (!inPuzzleBounds(i, j, PuzzleDimension))
            return;
        if (value > 0 && value <= PuzzleDigits) {
            rowUsed[i].add(value);
            columnUsed[j].add(value);
            getNonet(i, j).add(value);
        }
        puzzle[i][j] = value;
    }

    public RBoolInt getPuzzleValue(int i, int j) {
        if (inPuzzleBounds(i, j, PuzzleDimension)) {
            return new RBoolInt(true, puzzle[i][j]);
        } else {
            return new RBoolInt(false, -1);
        }
    }

    public void unsetPuzzleValue(int i, int j) {
        if (!inPuzzleBounds(i, j, PuzzleDimension))
            return;

        var value = puzzle[i][j];
        rowUsed[i].remove(value);
        columnUsed[j].remove(value);
        getNonet(i, j).remove(value);
        puzzle[i][j] = 0;
    }

    public boolean loadData(String text) {
        var digits = Util.getDigits(text);
        if (digits.length < PuzzleDimension * PuzzleDimension)
            return false;
        for (int i=0; i < digits.length; i++) {
            var digit = digits[i];
            var row = i / PuzzleDimension;
            var col = i % PuzzleDimension;
            setPuzzleValue(row, col, digit);
        }
        return true;
    }

    record RIntIntBool(int row, int col, boolean available) {}

    public RIntIntBool findNextUnfilled(int row, int col) {
        for (int pos = row*PuzzleDimension + col; pos < PuzzleDimension*PuzzleDimension; pos++) {
            var posRow = pos / PuzzleDimension;
            var posCol = pos % PuzzleDimension;
            if (puzzle[posRow][posCol] == 0) {
                return new RIntIntBool(posRow, posCol, true);
            }
        }
        return new RIntIntBool(-1, -1, false);
    }

    public boolean isCandidatePosition(int row, int col, int value) {
        if (value > PuzzleDigits)
            return false;
        if (puzzle[row][col] != 0)
            return false;
        return !(rowUsed[row].contains(value) || columnUsed[col].contains(value) || getNonet(row, col).contains(value));
    }

    public boolean solve() {
        return play(0, 0);
    }

    public boolean play(int startRow, int startCol) {
        var result = findNextUnfilled(startRow, startCol);
        var row = result.row();
        var col = result.col();
        var available = result.available();

        if (!available)
            return checkPuzzleValidity();

        for (int digit = 1; digit <= PuzzleDigits; digit++) {
            if (isCandidatePosition(row, col, digit)) {
                setPuzzleValue(row, col, digit);
                var status = isFullWithSize();
                if (status.full()) {
                    return true;
                } else if (play(row, col)) {
                    return true;
                }
                unsetPuzzleValue(row, col);
            }
        }
        return false;
    }

    record SolutionStatus(boolean solved, int position) {}
    public SolutionStatus checkPuzzleSolutionAlignment(String puzzle, String solution) {
        if (puzzle.length() != solution.length())
            return new SolutionStatus(false, -1);

        for (int i=0; i < puzzle.length(); i++) {
            if (puzzle.charAt(i) == '0')
                continue;
            if (puzzle.charAt(i) != solution.charAt(i))
                return new SolutionStatus(false, i);
        }
        return new SolutionStatus(true, puzzle.length() );
    }

    public static void main(String args[]) {
        if (args.length < 2)
            return;

        var puzzle = args[0];
        var solution = args[1];

        var sudoku = new Sudoku();
        sudoku.loadData(puzzle);
        var unsolvedPuzzle = sudoku.getRepresentation();
        var result = sudoku.solve();
        var solvedPuzzle = sudoku.getRepresentation();
        System.out.println(solvedPuzzle);
    }
}

