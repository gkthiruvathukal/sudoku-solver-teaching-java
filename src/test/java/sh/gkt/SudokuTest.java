package sh.gkt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SudokuTest {
    @Test
    void getNonet() {
    }

    @Test
    void isFullWithSize() {
    }

    @Test
    void getRepresentation() {
        var sudoku = new Sudoku();
        var puzzle = "004300209005009001070060043006002087190007400050083000600000105003508690042910300";
        sudoku.loadData(puzzle);
        var representation = sudoku.getRepresentation();
        assertEquals(puzzle, representation);
    }

    @Test
    void checkPuzzleValidity() {
    }

    @Test
    void inPuzzleBounds() {
    }

    @Test
    void setPuzzleValue() {
    }

    @Test
    void getPuzzleValue() {
    }

    @Test
    void unsetPuzzleValue() {
    }

    @Test
    void loadData() {
    }

    @Test
    void findNextUnfilled() {
    }

    @Test
    void isCandidatePosition() {
    }

    @Test
    void solve() {
    }

    @Test
    void play() {
    }

    @Test
    void checkPuzzleSolutionAlignment() {
    }
}