package byow.lab13;

import edu.princeton.cs.algs4.StdDraw;
import org.junit.Test;

import java.awt.*;

public class ProgramTest {
    public static void main(String[] args) {
        var width = 40;
        var height = 40;
        StdDraw.setCanvasSize(width * 16, height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setPenRadius(0.01);
        StdDraw.line(0, 38, 40, 38);
        StdDraw.text(4, 39, "Round: 1");
        StdDraw.text(width / 2.0, 39, "Watch!");
        StdDraw.text(33, 39, "I believe in you!");

        StdDraw.show();
    }

    @Test
    public void test() {
        var seed = System.currentTimeMillis();
        MemoryGame game = new MemoryGame(40, 40, seed);
        System.out.println(game.generateRandomString(6));
    }
}
