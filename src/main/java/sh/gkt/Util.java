package sh.gkt;

public class Util {

    public record Digits(int consumed, int[] digits) {}
    public static Digits getDigits(String text) {
        var intDigits = new int[text.length()];

        for (int i=0; i < text.length(); i++) {
            try {
                intDigits[i] = Integer.parseInt(text.substring(i, i + 1));
            } catch (NumberFormatException numberFormatException) {
                System.out.println("Exception at " + i);
                return new Digits(i, intDigits);
            }
        }
        return new Digits(intDigits.length, intDigits);
    }
}
