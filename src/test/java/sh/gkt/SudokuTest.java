package sh.gkt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SudokuTest {

    @Test
    void tryKnownSolution() {
        var puzzle = "300401620100080400005020830057800000000700503002904007480530010203090000070006090";
        var solution = "398471625126385479745629831657813942914762583832954167489537216263198754571246398";
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
    void getRepresentation() {
        var sudoku = new Sudoku();
        var puzzle = "004300209005009001070060043006002087190007400050083000600000105003508690042910300";
        sudoku.loadData(puzzle);
        var representation = sudoku.getRepresentation();
        assertEquals(puzzle, representation);
    }

}
