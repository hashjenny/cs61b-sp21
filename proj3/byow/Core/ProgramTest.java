package byow.Core;

import org.junit.Test;

import static byow.Core.Engine.getSeed;

public class ProgramTest {

    @Test
    public void getSeedTest() {
        var seed = getSeed("n123sssww");
        System.out.println(seed);
        seed = getSeed("lww");
        System.out.println(seed);
        seed = getSeed("n123sss:q");
        System.out.println(seed);
    }
}
