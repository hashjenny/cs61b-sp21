package game2048;

import org.junit.Test;

public class TestOther extends TestUtils {
    @Test
    public void test1() {
        int[][] before = new int[][] {
                {0, 0, 0, 4},
                {0, 0, 0, 2},
                {0, 0, 0, 2},
                {0, 0, 4, 0},
        };
        int[][] after = new int[][] {
                {0, 0, 4, 4},
                {0, 0, 0, 4},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
        };

        model = new Model(before, 4, 0, false);
        String prevBoard = model.toString();
        boolean changed = model.tilt(Side.NORTH);
        checkChanged(Side.NORTH, true, changed);
        checkModel(after, 8, 0, prevBoard, Side.NORTH);
    }

    @Test
    public void test2() {
        int[][] before = new int[][] {
                {0, 4, 4, 4},
                {0, 0, 0, 8},
                {0, 0, 0, 16},
                {4, 0, 0, 0},
        };
        int[][] after = new int[][] {
                {4, 4, 4, 4},
                {0, 0, 0, 8},
                {0, 0, 0, 16},
                {0, 0, 0, 0},
        };

        model = new Model(before, 32, 0, false);
        String prevBoard = model.toString();
        boolean changed = model.tilt(Side.NORTH);
        checkChanged(Side.NORTH, true, changed);
        checkModel(after, 32, 0, prevBoard, Side.NORTH);
    }
}
