package sh.gkt;

/*
 * Note: Work in progress. Porting from my Go version!
 */
import java.util.*;
public class Sudoku {
    public final static int PuzzleDigits = 9;
    public final static int PuzzleDimension = 9;
    public final static int NonetDimension = 3;

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

    record IsFullState(boolean full, int size) {}
    public IsFullState isFullWithSize() {
        var size = 0;
        for (int i = 0; i < PuzzleDimension; i++) {
            for (int j = 0; j < PuzzleDimension; j++) {
                if (puzzle[i][j] > 0) {
                    size++;
                }
            }
        }
        return new IsFullState(size == PuzzleDimension*PuzzleDimension, size);
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

    record PuzzleValue(boolean valid, int value) {}
    public PuzzleValue getPuzzleValue(int i, int j) {
        if (inPuzzleBounds(i, j, PuzzleDimension)) {
            return new PuzzleValue(true, puzzle[i][j]);
        } else {
            return new PuzzleValue(false, -1);
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
        if (digits.consumed() < PuzzleDimension * PuzzleDimension)
            return false;
        for (int i=0; i < digits.consumed(); i++) {
            var digit = digits.digits()[i];
            var row = i / PuzzleDimension;
            var col = i % PuzzleDimension;
            setPuzzleValue(row, col, digit);
        }
        return true;
    }

    record NextUnfilled(int row, int col, boolean available) {}

    public NextUnfilled findNextUnfilled(int row, int col) {
        for (int pos = row*PuzzleDimension + col; pos < PuzzleDimension*PuzzleDimension; pos++) {
            var posRow = pos / PuzzleDimension;
            var posCol = pos % PuzzleDimension;
            if (puzzle[posRow][posCol] == 0) {
                return new NextUnfilled(posRow, posCol, true);
            }
        }
        return new NextUnfilled(-1, -1, false);
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
                show();
                var status = isFullWithSize();
                if (status.full()) {
                    return true;
                } else {
                    if (play(row, col)) return true;
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

    public void show() {
        System.out.println("----".repeat(PuzzleDimension+1) + "-");
        for (int i=0; i < puzzle.length; i++) {
            for (int j=0; j < puzzle[i].length; j++) {
                System.out.printf(" %d  ", puzzle[i][j]);
            }
            System.out.printf(" (%d)\n", rowUsed[i].size());
        }
        System.out.println("----".repeat(PuzzleDimension+1) + "-");
        for (int j =0; j < puzzle[0].length; j++) {
            System.out.printf("(%d) ", columnUsed[j].size());
        }
        System.out.println();
	System.out.println("Nonets:");
	for (int p = 0; p < nonet.length; p++) {
		for (int q=0 ; q < nonet[p].length; q++) {
			System.out.printf("(%d) ", nonet[p][q].size());
		}
		System.out.println();
	}
        System.out.println();
    }

    public static void main(String args[]) {
        System.out.println("Sudoku loves you.");

        var puzzle = "300401620100080400005020830057800000000700503002904007480530010203090000070006090";
        var solution = "";
        System.out.printf("Puzzle: %s\n", puzzle);
        System.out.printf("Solution: %s\n", solution);

        var sudoku = new Sudoku();
        sudoku.loadData(puzzle);
        var unsolvedPuzzle = sudoku.getRepresentation();
        sudoku.show();
        var result = sudoku.solve();
        var solvedPuzzle = sudoku.getRepresentation();
        System.out.println(solvedPuzzle);
    }
}

