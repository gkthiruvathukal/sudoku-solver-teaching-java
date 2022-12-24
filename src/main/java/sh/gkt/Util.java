package sh.gkt;

public class Util {
    public static int[] getDigits(String text) {
        var intDigits = new int[text.length()];

        for (int i=0; i < text.length(); i++) {
            intDigits[i] = Integer.parseInt(text.substring(i, i+1));
        }
        return intDigits;
    }
}
