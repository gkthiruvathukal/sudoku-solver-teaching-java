package sh.gkt;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;

public class SudokuInteractive {
    // TODO: Set this up a Scala-like Stream.
    public static void repl() {
        var reader = LineReaderBuilder.builder().build();
        var prompt = "> ";
        while (true) {
            var line = "";
            try {
                line = reader.readLine(prompt);
            } catch (UserInterruptException e) {
                // Ignore
            } catch (EndOfFileException e) {
                return;
            }
            System.out.println("out> " + line);
        }
    }
}
