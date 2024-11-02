package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    private static final int WIDTH = 90;
    private static final int HEIGHT = 60;
    private static final long SEED = System.currentTimeMillis();
    private static final Random RANDOM = new Random(SEED);

    private static final int HEXAGON_SIZE = 5;
    private static final int HEXAGON_WIDTH = 3 * HEXAGON_SIZE - 2;
    private static final int HEXAGON_TOP = HEXAGON_SIZE - 1;
    private static final int HEXAGON_BOTTOM = HEXAGON_SIZE;

    private static final HashSet<Point> POINT_SET = new HashSet<>();
    private static final HashSet<Point> VISITED = new HashSet<>();


    record Point(int x, int y) {
        public boolean isValid() {
            var east = x + HEXAGON_WIDTH - 1;
            var north = y + HEXAGON_TOP;
            var south = y - HEXAGON_BOTTOM;
            return x >= 0 && east < WIDTH && north < HEIGHT && south >= 0
                    && !VISITED.contains(this);
        }
    }

    public static void main(String[] args) {
        var render = new TERenderer();
        render.initialize(WIDTH, HEIGHT);

        var tiles = new TETile[WIDTH][HEIGHT];
        init(tiles);

        while (!POINT_SET.isEmpty()) {
            var list = new ArrayList<>(POINT_SET);
            var p = list.remove(0);
            POINT_SET.remove(p);
            drawHexagon(tiles, p);
        }
        render.renderFrame(tiles);
    }

    private static void init(TETile[][] tiles) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
        int posX = RANDOM.nextInt(WIDTH - HEXAGON_WIDTH);
        // random.nextInt(max - min + 1) + min
        int posY = RANDOM.nextInt((HEIGHT - HEXAGON_BOTTOM) - HEXAGON_TOP + 1) + HEXAGON_TOP;
        Point p = new Point(posX, posY);
        POINT_SET.add(p);
    }

    private static void drawHexagon(TETile[][] tiles, Point point) {
        var posX = point.x();
        var posY = point.y();
        int tileNum = RANDOM.nextInt(11);
        var tileType = switch (tileNum) {
            case 0 -> Tileset.WALL;
            case 1 -> Tileset.AVATAR;
            case 2 -> Tileset.FLOOR;
            case 3 -> Tileset.FLOWER;
            case 4 -> Tileset.GRASS;
            case 5 -> Tileset.LOCKED_DOOR;
            case 6 -> Tileset.MOUNTAIN;
            case 7 -> Tileset.WATER;
            case 8 -> Tileset.SAND;
            case 9 -> Tileset.TREE;
            default -> Tileset.UNLOCKED_DOOR;
        };
        // draw middle
        for (int x = posX; x < posX + HEXAGON_WIDTH; x += 1) {
            for (int y = posY; y > posY - 2; y -= 1) {
                tiles[x][y] = tileType;
            }
        }
        // the top of middle
        int xStart = posX + 1;
        int xEnd = posX + HEXAGON_WIDTH - 2;
        for (int y = posY + 1; y <= posY + HEXAGON_TOP; y += 1) {
            for (int x = xStart; x <= xEnd; x += 1) {
                tiles[x][y] = tileType;
            }
            xStart += 1;
            xEnd -= 1;
        }
        // the bottom of middle
        xStart = posX + 1;
        xEnd = posX + HEXAGON_WIDTH - 2;
        for (int y = posY - 2; y >= posY - HEXAGON_BOTTOM; y -= 1) {
            for (int x = xStart; x <= xEnd; x += 1) {
                tiles[x][y] = tileType;
            }
            xStart += 1;
            xEnd -= 1;
        }
        VISITED.add(point);
        getNearPoints(point);
    }

    private static void getNearPoints(Point p) {
        int x = p.x();
        int y = p.y();
        var prePoint = new ArrayList<Point>();
        var north = new Point(x, y + HEXAGON_SIZE * 2);
        var south = new Point(x, y - HEXAGON_SIZE * 2);
        var northeast = new Point(x + HEXAGON_WIDTH - HEXAGON_SIZE + 1, y + HEXAGON_TOP + 1);
        var southeast = new Point(x + HEXAGON_WIDTH - HEXAGON_SIZE + 1, y - HEXAGON_BOTTOM);
        var northwest = new Point(x - HEXAGON_WIDTH + HEXAGON_SIZE - 1, y + HEXAGON_TOP + 1);
        var southwest = new Point(x - HEXAGON_WIDTH + HEXAGON_SIZE - 1, y - HEXAGON_BOTTOM);

        prePoint.add(north);
        prePoint.add(south);
        prePoint.add(northeast);
        prePoint.add(southeast);
        prePoint.add(northwest);
        prePoint.add(southwest);
        for (Point point : prePoint) {
            if (point.isValid()) {
                POINT_SET.add(point);
            }
        }
    }

}
