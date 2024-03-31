package flik;


import org.junit.Assert;
import org.junit.Test;

public class FlikTest {
    @Test
    public void flikTest() {
        int i = 0;
        for (int j = 0; i < 500; ++i, ++j) {
            System.out.println(i + "  " + j);
            Assert.assertTrue(Flik.isSameNumber(i, j));
        }
    }
}
