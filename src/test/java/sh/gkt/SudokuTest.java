package sh.gkt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SudokuTest {

    private static final int KAGGLE_SAMPLE_SIZE = 100;
    @Test
    void tryKnownSolution() {
        var solutionId = 10;
        assert(KaggleSudokuSampleData.puzzles.length >= solutionId);
        var puzzle = KaggleSudokuSampleData.puzzles[solutionId];
        var solution = KaggleSudokuSampleData.solutions[solutionId];
        System.out.printf("  Puzzle: %s\n", puzzle);
        System.out.printf("Solution: %s\n", solution);

        var sudoku = new Sudoku();
        sudoku.loadData(puzzle);
        var solved = sudoku.solve();
        var result = sudoku.getRepresentation();
        System.out.printf("  Result: %s\n", result);
        assertEquals(solution, result);
        assertTrue(solved);
    }

    @Test
    void tryKaggleSubset() {
        assertEquals(KaggleSudokuSampleData.puzzles.length, KaggleSudokuSampleData.solutions.length);

        var skip = KaggleSudokuSampleData.puzzles.length / KAGGLE_SAMPLE_SIZE;
        for (int i = 0; i < KaggleSudokuSampleData.puzzles.length; i += skip) {
            var puzzle = KaggleSudokuSampleData.puzzles[i];
            var solution = KaggleSudokuSampleData.solutions[i];
            System.out.printf("  Puzzle: %s\n", puzzle);
            System.out.printf("Solution: %s\n", solution);

            var sudoku = new Sudoku();
            sudoku.loadData(puzzle);
            var solved = sudoku.solve();
            var result = sudoku.getRepresentation();
            System.out.printf("  Result: %s\n", result);
            assertEquals(solution, result);
            assertTrue(solved);
        }
    }
    @Test
    void getRepresentation() {
        assert(KaggleSudokuSampleData.puzzles.length > 0);
        var sudoku = new Sudoku();
        var puzzle = KaggleSudokuSampleData.puzzles[0];
        sudoku.loadData(puzzle);
        var representation = sudoku.getRepresentation();
        assertEquals(puzzle, representation);
    }
}
