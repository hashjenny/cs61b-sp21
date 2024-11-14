package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    private final ArrayList<Rectangle> rectangles = new ArrayList<>();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    public final static int RECTANGLE_MAX_WIDTH = WIDTH / 8;
    public final static int RECTANGLE_MIN_WIDTH = 3;
    public final static int RECTANGLE_MAX_HEIGHT = HEIGHT / 5;
    public final static int RECTANGLE_MIN_HEIGHT = 3;

    public final static TETile WALL = Tileset.WALL;
    public final static TETile BASE = Tileset.FLOWER;
    public final static TETile HALL = Tileset.SAND;
    public final static TETile PERSON = Tileset.AVATAR;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        var seed = getSeed(input);
        Random rand = new Random(seed);
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        init(finalWorldFrame);

        Rectangle firstRectangle = Rectangle.getRandomRectangle(rand);
        firstRectangle.draw(finalWorldFrame);
        rectangles.add(firstRectangle);
        extendWorld(finalWorldFrame, firstRectangle, rand);

        for (int i = 0; i < 5; i++) {
            var rect = getRandomItem(rectangles, rand);
            extendWorld(finalWorldFrame, rect, rand);
        }

        walk(finalWorldFrame, rand);

        return finalWorldFrame;
    }

    public static long getSeed(String input) {
        var inputSeries = input.toCharArray();
        var command = Character.toLowerCase(inputSeries[0]);
        var seedBuilder = new StringBuilder();
        var digitFlag = true;
        for (int i = 1; i < inputSeries.length; i++) {
            if (digitFlag && Character.isDigit(inputSeries[i])) {
                seedBuilder.append(inputSeries[i]);
            } else {
                digitFlag = false;

            }
        }
        var seedStr = seedBuilder.toString();
        if (seedStr.isEmpty()) {
            return 0;
        }
        return Long.parseLong(seedStr);
    }

    private static void init(TETile[][] tiles) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    public void render(TETile[][] tiles) {
        ter.renderFrame(tiles);
    }

    public void extendWorld(TETile[][] tiles, Rectangle firstRec, Random rand) {
        var rec = firstRec;
        while (rec != null) {
            // System.out.println("current rectangle:" + rec);
            var newWidth = Rectangle.getRandomWidth(rand);
            var newHeight = Rectangle.getRandomHeight(rand);
            var validPoints = rec.getNearValidPoints(tiles, newWidth, newHeight);
            if (!validPoints.isEmpty()) {
                var point = getRandomItem(validPoints, rand);
                var newRec = new Rectangle(newWidth, newHeight, point);
                newRec.draw(tiles);
                rec.addNearPair(newRec);
                rec = newRec;
                rectangles.add(newRec);
            } else {
                rec = null;
            }
        }
    }

    public <T> T getRandomItem(List<T> list, Random rand) {
        var index = rand.nextInt(list.size());
        return list.get(index);
    }

    // public boolean isValidHall(TETile[][] tiles, Point p) {
    //     var x = p.x();
    //     var y = p.y();
    //     // 九宫格内都不是NOTHING
    //     return tiles[x + 1][y].character() != ' ' && tiles[x - 1][y].character() != ' '
    //             && tiles[x][y + 1].character() != ' ' && tiles[x][y - 1].character() != ' '
    //             && tiles[x - 1][y + 1].character() != ' ' && tiles[x + 1][y - 1].character() != ' '
    //             && tiles[x + 1][y + 1].character() != ' ' && tiles[x - 1][y - 1].character() != ' '
    //             && tiles[x][y].character() != ' ';
    // }
    //
    // public boolean isValidHall(TETile[][] tiles, int x, int y) {
    //     return isValidHall(tiles, new Point(x, y));
    // }

    public void walk(TETile[][] world, Random rand) {
        for (var rect : rectangles) {
            world[rect.getBasePoint().x()][rect.getBasePoint().y()] = BASE;
            generateHall(world, rect, rand);
        }
    }

    public void generateHall(TETile[][] world, Rectangle rect, Random rand) {
        var start = rect.getHallPoint();
        for (var entry : rect.getPairs().entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            var end = rect.getNearInnerPoint(key);

            Point.drawHall(world, start);
            Point.drawHall(world, end);
            Point.drawHall(world, key);
            Point.drawHall(world, value);

            var x = start.x();
            var y = start.y();
            var endX = end.x();
            var endY = end.y();
            var desX = start.x() < end.x() ? 1 : -1;
            var desY = start.y() < end.y() ? 1 : -1;
            while (x != endX) {
                x = x + desX;
                Point.drawHall(world, x, y);
            }
            while (y != endY) {
                y = y + desY;
                Point.drawHall(world, x, y);
            }
        }

    }

}
