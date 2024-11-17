package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    private final ArrayList<Rectangle> rectangles = new ArrayList<>();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    public static final int RECTANGLE_MAX_WIDTH = WIDTH / 8;
    public static final int RECTANGLE_MIN_WIDTH = 3;
    public static final int RECTANGLE_MAX_HEIGHT = HEIGHT / 5;
    public static final int RECTANGLE_MIN_HEIGHT = 3;
    // nothing: ' ', wall: '#', hall: '·', person: '@'
    public static final String WORLD_FILE = "world.txt";

    private final Person person = new Person(0, 0);
    private long seed = 0;
    private boolean isQuit = false;
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
        // Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        var command = new Command(input);
        seed = command.getSeed();
        var rand = new Random(seed);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        init(world);

        switch (command.getKey()) {
            case 'n':
                generateWorld(world, rand, command);
                break;
            case 'l':
                loadWorld(world, rand);
                break;
            case 'r':
            default:
                break;
        }
        return world;
    }


    private static void init(TETile[][] tiles) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    public void render(TETile[][] world, String clickName) {
        if (isQuit) {
            System.exit(0);
        }
        ter.renderFrame(world);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(2, HEIGHT - 2, clickName);
        StdDraw.show();
    }

    public void start(TETile[][] world) throws InterruptedException {
        ter.initialize(WIDTH, HEIGHT);
        render(world, "");
        while (true) {
            waitForInput(world);
            String clickName = waitForClick(world);
            Thread.sleep(80);
            render(world, clickName);
        }
    }

    private void waitForInput(TETile[][] world) {
        if (StdDraw.hasNextKeyTyped()) {
            var c = StdDraw.nextKeyTyped();
            action(world, c);
        }
    }

    private String waitForClick(TETile[][] world) {
        String clickName = "";
        if (StdDraw.isMousePressed()) {
            var x = (int) StdDraw.mouseX();
            var y = (int) StdDraw.mouseY();
            clickName = characterToTileName(world[x][y].character());
        }
        return clickName;
    }

    public void action(TETile[][] world, char c) {
        switch (c) {
            case 'w':
                person.move(world, person.getX(), person.getY() + 1);
                break;
            case 's':
                person.move(world, person.getX(), person.getY() - 1);
                break;
            case 'a':
                person.move(world, person.getX() - 1, person.getY());
                break;
            case 'd':
                person.move(world, person.getX() + 1, person.getY());
                break;
            case ':':
                isQuit = true;
                break;
            case 'q':
                if (isQuit) {
                    saveWorld(world);
                }
                break;
            default:
                break;
        }
    }

    public void loadWorld(TETile[][] world, Random rand) {
        try (var br = new BufferedReader(new FileReader(WORLD_FILE))) {
            String line;
            int lineCounter = 0;
            while ((line = br.readLine()) != null) {
                if (lineCounter == 0) {
                    seed = Long.parseLong(line);
                    rand.setSeed(seed);
                } else {
                    var characterSeries = line.toCharArray();
                    if (lineCounter <= HEIGHT) {
                        for (int x = 0; x < WIDTH; x += 1) {
                            world[x][HEIGHT - lineCounter] = characterToTile(characterSeries[x]);
                        }
                    }
                }
                lineCounter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveWorld(TETile[][] world) {
        try (var bw = new BufferedWriter(new FileWriter(WORLD_FILE))) {
            bw.write(Long.toString(seed));
            bw.newLine();
            bw.write(TETile.toString(world));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateWorld(TETile[][] world, Random rand, Command command) {
        // draw wall
        Rectangle firstRectangle = Rectangle.getRandomRectangle(rand);
        firstRectangle.draw(world);
        rectangles.add(firstRectangle);
        extendWorld(world, firstRectangle, rand);
        for (int i = 0; i < 5; i++) {
            var rect = getRandomItem(rectangles, rand);
            extendWorld(world, rect, rand);
        }
        // draw floor
        generateFloor(world);
        // draw person
        var pos = firstRectangle.getStartPoint();
        person.setPos(pos);
        Point.drawPerson(world, person.getX(), person.getY());
        // draw action
        if (command != null) {
            for (var c : command.getActions()) {
                action(world, c);
            }
        }
    }

    public void extendWorld(TETile[][] tiles, Rectangle firstRec, Random rand) {
        var rec = firstRec;
        while (rec != null) {
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


    public void generateFloor(TETile[][] world) {
        for (var rect : rectangles) {
            // world[rect.getBasePoint().x()][rect.getBasePoint().y()] = BASE;
            generateFloorForeach(world, rect);
        }
    }

    public void generateFloorForeach(TETile[][] world, Rectangle rect) {
        var start = rect.getStartPoint();
        for (var entry : rect.getPairs().entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            var end = rect.getNearInnerPoint(key);

            Point.drawFloor(world, start);
            Point.drawFloor(world, end);
            Point.drawFloor(world, key);
            Point.drawFloor(world, value);

            var x = start.x();
            var y = start.y();
            var endX = end.x();
            var endY = end.y();
            var desX = start.x() < end.x() ? 1 : -1;
            var desY = start.y() < end.y() ? 1 : -1;
            while (x != endX) {
                x = x + desX;
                Point.drawFloor(world, x, y);
            }
            while (y != endY) {
                y = y + desY;
                Point.drawFloor(world, x, y);
            }
        }

    }

    public static TETile characterToTile(char c) {
        switch (c) {
            case '#':
                return Tileset.WALL;
            case '·':
                return Tileset.FLOOR;
            case '@':
                return Tileset.AVATAR;
            default:
                return Tileset.NOTHING;
        }
    }

    public static String characterToTileName(char c) {
        switch (c) {
            case '#':
                return "Wall";
            case '·':
                return "Floor";
            case '@':
                return "Person";
            default:
                return "Nothing";
        }
    }

}
