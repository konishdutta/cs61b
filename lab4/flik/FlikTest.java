package flik;
import org.junit.Test;
import static org.junit.Assert.*;

public class FlikTest {
    @Test
    public void testOne() {
        int x = 128;
        int y = 128;
        assertTrue(Flik.isSameNumber(x, y));
    }

    @Test
    public void testTwo() {
        int x = 127;
        int y = 127;
        assertTrue(Flik.isSameNumber(x, y));
    }

    @Test
    public void HorribleSteve() {
        int i = -10000;

        for (int j = -10000; i < 10000; ++i, ++j) {
            System.out.println(i + " " + j);
            assertTrue(Flik.isSameNumber(i, j));
        }
    }

}
