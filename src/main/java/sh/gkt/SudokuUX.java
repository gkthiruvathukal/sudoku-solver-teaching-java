package sh.gkt;

import org.tinylog.Logger;


// PicoCLI rocks!
// This is based on the subcommand documentation.

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(name = "SudokuUX", subcommands = {Solve.class})
class SudokuUX implements Callable<Integer> {
    // global options can be added here. Might use this for -puzzle and -solution
    // @Option(names = "-x") int x;

    @Override public Integer call() {
        System.out.printf("Try a command, e.g. solve");
        boolean ok = true;
        return 0; // exit code
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new SudokuUX()).execute(args);
        System.exit(exitCode);
    }
}

@Command(name = "solve", description = "I'm the solve subcommand")
class Solve implements Callable<Integer> {
    public static final String KNOWN_PUZZLE = "300401620100080400005020830057800000000700503002904007480530010203090000070006090";
    public static final String KNOWN_SOLUTION = "398471625126385479745629831657813942914762583832954167489537216263198754571246398";

    // known puzzle and solution are defaults
    @Option(names = "-puzzle") String puzzle = KNOWN_PUZZLE;
    @Option(names = "-solution") String solution;

    @Option(names = "-progress") Boolean progress = false;

    @Override public Integer call() {
        if (puzzle.equals(KNOWN_PUZZLE)) {
            solution = KNOWN_SOLUTION;
        }
        var sudoku = new Sudoku();
        if (progress)
            sudoku.setProgress();
        sudoku.loadData(puzzle);
        var solved = sudoku.solve();
        if (solved) {
            var solvedPuzzle = sudoku.getRepresentation();
            System.out.println(solvedPuzzle);
            if (solvedPuzzle.equals(solution)) {
                System.out.println("Solution matched!");
            }
        }
        return 0; // exit code
    }
}