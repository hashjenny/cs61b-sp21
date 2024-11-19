package byow.Core;

import byow.Networking.BYOWServer;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Engine {
    TERenderer ter = new TERenderer();

    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    public static final int ALL_HEIGHT = 32;
    public static final int RECTANGLE_MAX_WIDTH = WIDTH / 8;
    public static final int RECTANGLE_MIN_WIDTH = 3;
    public static final int RECTANGLE_MAX_HEIGHT = HEIGHT / 5;
    public static final int RECTANGLE_MIN_HEIGHT = 3;

    // nothing: ' ', wall: '#', hall: '·', person: '@'
    public static final String WORLD_FILE = "world.txt";

    private final ArrayList<Rectangle> rectangles = new ArrayList<>();
    private final Person person = new Person(0, 0);
    private long seed = 0;
    private boolean commandMode = false;
    private boolean isQuit = false;

    // replay
    private boolean isReplay = false;
    private Point startPoint;
    private TETile[][] initWorld;
    private final ArrayList<Character> history = new ArrayList<>();

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() throws InterruptedException {
        long seed = 0;
        var rand = new Random(seed);
        var world = new TETile[WIDTH][HEIGHT];
        init(world);

        ter.initialize(WIDTH, ALL_HEIGHT);
        startScreen(world, rand);
        render(world, "");
        gameLoop(world);
    }

    private void inputSeed(TETile[][] world, Random rand) {
        var noSeed = true;
        var seedBuilder = new StringBuilder();
        while (noSeed) {
            StdDraw.clear(Color.BLACK);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text((double) WIDTH / 2, (double) ALL_HEIGHT / 2,
                    "Input seed: " + seedBuilder);
            StdDraw.text((double) WIDTH / 2, (double) ALL_HEIGHT / 2 - 2,
                    "(Type 'S' to stop input seed.)");
            StdDraw.show();
            if (StdDraw.hasNextKeyTyped()) {
                var key = StdDraw.nextKeyTyped();
                if (key != 's') {
                    seedBuilder.append(key);
                } else {
                    noSeed = false;
                    var seedStr = seedBuilder.toString();
                    if (seedStr.isEmpty()) {
                        rand.setSeed(0);
                    } else {
                        rand.setSeed(Long.parseLong(seedStr));
                    }
                }
            }
        }
        generateWorld(world, rand);
    }

    private void startScreen(TETile[][] world, Random rand) {
        var startFlag = true;
        while (startFlag) {
            StdDraw.clear(Color.BLACK);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text((double) WIDTH / 2, (double) ALL_HEIGHT * 2 / 3, "CS61B: THE GAME");
            StdDraw.text((double) WIDTH / 2, (double) ALL_HEIGHT * 2 / 3 - 4, "New Game (N)");
            StdDraw.text((double) WIDTH / 2, (double) ALL_HEIGHT * 2 / 3 - 8, "Load Game (L)");
            StdDraw.text((double) WIDTH / 2, (double) ALL_HEIGHT * 2 / 3 - 12, "Quit (Q)");
            StdDraw.show();
            if (StdDraw.hasNextKeyTyped()) {
                var key = StdDraw.nextKeyTyped();
                switch (key) {
                    case 'n':
                        inputSeed(world, rand);
                        startFlag = false;
                        break;
                    case 'l':
                        loadWorld(world, rand);
                        startFlag = false;
                        break;
                    case 'q':
                        System.exit(0);
                        break;
                    default:
                        break;
                }
            }
        }
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
                generateWorld(world, rand);
                executeCommand(world, command);
                break;
            case 'l':
                loadWorld(world, rand);
                executeCommand(world, command);
                break;
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

    public void render(TETile[][] world, String mousePointTo) {
        if (isQuit) {
            System.exit(0);
        }
        ter.renderFrame(world);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(5, ALL_HEIGHT - 1, mousePointTo);
        var current = LocalDateTime.now();
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        var timeString = current.format(formatter);
        StdDraw.text(WIDTH - 6, ALL_HEIGHT - 1, timeString);
        StdDraw.show();
    }

    public void action(TETile[][] world, char c) {
        switch (c) {
            case 'W':
            case 'w':
                person.move(world, person.getX(), person.getY() + 1);
                if (!isReplay) {
                    history.add(c);
                }
                break;
            case 'S':
            case 's':
                person.move(world, person.getX(), person.getY() - 1);
                if (!isReplay) {
                    history.add(c);
                }
                break;
            case 'A':
            case 'a':
                person.move(world, person.getX() - 1, person.getY());
                if (!isReplay) {
                    history.add(c);
                }
                break;
            case 'D':
            case 'd':
                person.move(world, person.getX() + 1, person.getY());
                if (!isReplay) {
                    history.add(c);
                }
                break;
            case 'R':
            case 'r':
                isReplay = true;
                break;
            case ':':
                commandMode = true;
                break;
            case 'Q':
            case 'q':
                if (commandMode) {
                    saveWorld(world);
                    commandMode = false;
                    isQuit = true;
                }
                break;
            default:
                break;
        }
    }

    private void waitForInput(TETile[][] world) {
        if (StdDraw.hasNextKeyTyped()) {
            var c = StdDraw.nextKeyTyped();
            action(world, c);
        }
    }

    private String waitForMouse(TETile[][] world) {
        String here = "";
        var x = (int) StdDraw.mouseX();
        var y = (int) StdDraw.mouseY();
        if (y >= 0 && y < HEIGHT && x >= 0 && x < WIDTH) {
            here = characterToTileName(world[x][y].character());
        }
        return here;
    }

    public void start(TETile[][] world) throws InterruptedException {
        ter.initialize(WIDTH, ALL_HEIGHT);
        render(world, "");
        gameLoop(world);
    }

    private void gameLoop(TETile[][] world) throws InterruptedException {
        while (true) {
            if (!isReplay) {
                waitForInput(world);
                String here = waitForMouse(world);
                Thread.sleep(80);
                render(world, here);
            } else {
                replay();
            }
        }
    }

    private void replay() throws InterruptedException {
        if (!history.isEmpty()) {
            var temp = TETile.copyOf(initWorld);
            person.setPos(startPoint);
            render(initWorld, "Replay Mode");
            for (var c : history) {
                action(initWorld, c);
                Thread.sleep(500);
                render(initWorld, "Replay Mode");
            }
            isReplay = false;
            initWorld = TETile.copyOf(temp);
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
                            if (characterSeries[x] == '@') {
                                startPoint = new Point(x, HEIGHT - lineCounter);
                                person.setPos(new Point(x, HEIGHT - lineCounter));
                            }
                            world[x][HEIGHT - lineCounter] = characterToTile(characterSeries[x]);
                        }
                    }
                }
                lineCounter++;
            }
            initWorld = TETile.copyOf(world);
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

    public void generateWorld(TETile[][] world, Random rand) {
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

        // init state
        startPoint = pos;
        initWorld = TETile.copyOf(world);
    }

    public void executeCommand(TETile[][] world, Command command) {
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

    public <T> T getRandomItem(List<T> list, Random rand) {
        var index = rand.nextInt(list.size());
        return list.get(index);
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

    // game sharing
    public void interactWithRemoteClient(String port) throws IOException, InterruptedException {
        var server = new BYOWServer(Integer.parseInt(port));

        long seed = 0;
        var rand = new Random(seed);
        var world = new TETile[WIDTH][HEIGHT];
        init(world);


        server.sendCanvasConfig(WIDTH * 16, ALL_HEIGHT * 16);
        ter.initialize(WIDTH, ALL_HEIGHT);

        startScreenRemote(world, rand, server);
        renderRemote(world, "", server);
        gameLoopRemote(world, server);
    }

    private void startScreenRemote(TETile[][] world, Random rand, BYOWServer server) {
        var startFlag = true;
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text((double) WIDTH / 2, (double) ALL_HEIGHT * 2 / 3, "CS61B: THE GAME (REMOTE)");
        StdDraw.text((double) WIDTH / 2, (double) ALL_HEIGHT * 2 / 3 - 4, "New Game (N)");
        StdDraw.text((double) WIDTH / 2, (double) ALL_HEIGHT * 2 / 3 - 8, "Load Game (L)");
        StdDraw.text((double) WIDTH / 2, (double) ALL_HEIGHT * 2 / 3 - 12, "Quit (Q)");
        StdDraw.show();
        server.sendCanvas();
        while (startFlag) {
            if (server.clientHasKeyTyped()) {
                var key = server.clientNextKeyTyped();
                switch (key) {
                    case 'n':
                        inputSeedRemote(world, rand, server);
                        startFlag = false;
                        break;
                    case 'l':
                        loadWorld(world, rand);
                        startFlag = false;
                        break;
                    case 'q':
                        System.exit(0);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void inputSeedRemote(TETile[][] world, Random rand, BYOWServer server) {
        var noSeed = true;
        var seedBuilder = new StringBuilder();
        while (noSeed) {
            StdDraw.clear(Color.BLACK);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text((double) WIDTH / 2, (double) ALL_HEIGHT / 2,
                    "Input seed: " + seedBuilder);
            StdDraw.text((double) WIDTH / 2, (double) ALL_HEIGHT / 2 - 2,
                    "(Type 'S' to stop input seed.)");
            StdDraw.show();
            server.sendCanvas();
            if (server.clientHasKeyTyped()) {
                var key = server.clientNextKeyTyped();
                if (key != 's') {
                    seedBuilder.append(key);
                } else {
                    noSeed = false;
                    var seedStr = seedBuilder.toString();
                    if (seedStr.isEmpty()) {
                        rand.setSeed(0);
                    } else {
                        rand.setSeed(Long.parseLong(seedStr));
                    }
                }
            }
        }
        generateWorld(world, rand);
    }

    public void renderRemote(TETile[][] world, String mousePointTo, BYOWServer server) {
        render(world, mousePointTo);
        server.sendCanvas();
    }

    private void gameLoopRemote(TETile[][] world, BYOWServer server) throws InterruptedException {
        while (true) {
            if (!isReplay) {
                waitForInputRemote(world, server);
                Thread.sleep(80);
                renderRemote(world, "", server);
            } else {
                replayRemote(server);
            }
        }
    }

    private void replayRemote(BYOWServer server) throws InterruptedException {
        if (!history.isEmpty()) {
            var temp = TETile.copyOf(initWorld);
            person.setPos(startPoint);
            renderRemote(initWorld, "Replay Mode", server);
            for (var c : history) {
                action(initWorld, c);
                Thread.sleep(500);
                renderRemote(initWorld, "Replay Mode", server);
            }
            isReplay = false;
            initWorld = TETile.copyOf(temp);
        }
    }

    private void waitForInputRemote(TETile[][] world, BYOWServer server) {
        if (server.clientHasKeyTyped()) {
            var key = server.clientNextKeyTyped();
            action(world, key);
        }
    }

}
