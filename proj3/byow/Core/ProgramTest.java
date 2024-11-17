package byow.Core;

import org.junit.Test;


public class ProgramTest {

    @Test
    public void getSeedTest() {
        var seed = new Command("n123sssww").getSeed();
        System.out.println(seed);
        seed = new Command("lww").getSeed();
        System.out.println(seed);
        seed = new Command("n123sss:q").getSeed();
        System.out.println(seed);
    }

    @Test
    public void nearTest() {
        var rect1 = new Rectangle(6, 6, new Point(0, 0));
        var rect2 = new Rectangle(5, 4, new Point(6, 2));
        rect1.addNearPair(rect2);


        var rect3 = new Rectangle(3, 3, new Point(1, 6));
        rect1.addNearPair(rect3);
        for (var entry : rect1.getPairs().entrySet()) {
            var near = rect1.getNearInnerPoint(entry.getKey());
            System.out.println(entry.getKey() + " " + entry.getValue() + " " + near);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Engine engine = new Engine();
        var world = engine.interactWithInputString(args[1]);
        engine.start(world);
    }


}
