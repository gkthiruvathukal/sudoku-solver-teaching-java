package sh.gkt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

    @Test
    void getDigitsNormal() {
        var text1 = "8675309";
        var digits1 = new int[]{8, 6, 7, 5, 3, 0, 9};
        var result1 = Util.getDigits(text1);
        assertEquals(text1.length(), result1.consumed());
        assertArrayEquals(result1.digits(), digits1);
    }

    @Test
    void getDigitsEmpty() {
        var text2 = "";
        var digits2 = new int[]{};
        var result2 = Util.getDigits(text2);
        assertEquals(0, result2.consumed());
        assertArrayEquals(result2.digits(), digits2);
    }


    @Test
    void getDigitsAltogetherBad() {
        var text3 = "George";
        var result3 = Util.getDigits(text3);
        assertEquals(0, result3.consumed());
    }

    @Test
    void getDigitsGoodThenBad() {
        var text4 = "100George";
        var digits4 = new int[] { 1, 0, 0 };
        var result4 = Util.getDigits(text4);
        System.out.println("Digits4: " +  result4);
        assertEquals(result4.consumed(), digits4.length);
        for (int i=0; i < digits4.length; i++)
            assertEquals(result4.digits()[i], digits4[i]);
    }
}