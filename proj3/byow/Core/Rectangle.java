package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Random;

import static byow.Core.Engine.HEIGHT;
import static byow.Core.Engine.WIDTH;

public class Rectangle {
    private final int width;
    private final int height;
    private final Point basePoint;
    private final ArrayList<Point> rectanglePoints;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Point getBasePoint() {
        return basePoint;
    }

    public ArrayList<Point> getRectanglePoints() {
        return rectanglePoints;
    }

    public Rectangle(int width, int height, Point p) {
        this.width = width;
        this.height = height;
        this.basePoint = p;

        this.rectanglePoints = new ArrayList<>();
        var x = basePoint.x();
        var y = basePoint.y();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                rectanglePoints.add(new Point(x + i, y + j));
            }
        }
    }

    @Override
    public String toString() {
        return "Rectangle{"
                + "width=" + width
                + ", height=" + height
                + ", basePoint={" + basePoint.x() + "," + basePoint.y() + "}";
    }

    public boolean contains(Point p) {
        return rectanglePoints.contains(p);
    }

    public boolean isValid(TETile[][] world) {
        for (Point p : rectanglePoints) {
            if (!p.isValid(world)) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<Point> getNearValidPoints(TETile[][] world, int w, int h) {
        var nearPoints = new ArrayList<Point>();
        // east & west
        for (int i = basePoint.y() + RECTANGLE_MIN_HEIGHT - h; i < basePoint.y() + height - RECTANGLE_MIN_HEIGHT; i++) {
            nearPoints.add(new Point(basePoint.x() + width, i));
            nearPoints.add(new Point(basePoint.x() - w, i));
        }
        // north & south
        for (int i = basePoint.x() + RECTANGLE_MIN_WIDTH - w; i < basePoint.x() + width - RECTANGLE_MIN_WIDTH; i++) {
            nearPoints.add(new Point(i, basePoint.y() + height));
            nearPoints.add(new Point(i, basePoint.y() - h));
        }

        var validPoints = new ArrayList<Point>();
        for (Point p : nearPoints) {
            if (p.isValid(world) && new Rectangle(w, h, p).isValid(world)) {
                validPoints.add(p);
            }
        }
        return validPoints;
    }

    public void drawWithEdge(TETile[][] world) {
        for (int i = basePoint.x(); i < basePoint.x() + width; i++) {
            for (int j = basePoint.y(); j < basePoint.y() + height; j++) {
                if (i == basePoint.x() || i == basePoint.x() + width - 1
                        || j == basePoint.y() || j == basePoint.y() + height - 1) {
                    world[i][j] = Tileset.WATER;
                } else {
                    world[i][j] = Tileset.WALL;
                }
            }
        }

    }

    public void draw(TETile[][] world) {
        for (var p : rectanglePoints) {
            world[p.x()][p.y()] = Tileset.WALL;
        }
    }

    private final static int RECTANGLE_MAX_WIDTH = WIDTH / 8;
    private final static int RECTANGLE_MIN_WIDTH = 3;
    private final static int RECTANGLE_MAX_HEIGHT = HEIGHT / 5;
    private final static int RECTANGLE_MIN_HEIGHT = 3;


    public static int getRandomLength(Random rand, int min, int max) {
        return RandomUtils.uniform(rand, min, max);
    }

    public static int getRandomWidth(Random rand) {
        return getRandomLength(rand, RECTANGLE_MIN_WIDTH, RECTANGLE_MAX_WIDTH);
    }

    public static int getRandomHeight(Random rand) {
        return getRandomLength(rand, RECTANGLE_MIN_HEIGHT, RECTANGLE_MAX_HEIGHT);
    }

    public static Point getRandomPoint(Random rand) {
        var x = RandomUtils.uniform(rand, 1, WIDTH - 1 - RECTANGLE_MIN_WIDTH);
        var y = RandomUtils.uniform(rand, 1, HEIGHT - 1 - RECTANGLE_MIN_HEIGHT);
        return new Point(x, y);
    }

    public static Rectangle getRandomRectangle(Random rand) {
        var base = getRandomPoint(rand);
        var width = getRandomLength(rand, RECTANGLE_MIN_WIDTH, Math.min(RECTANGLE_MAX_WIDTH, WIDTH - base.x()));
        var height = getRandomLength(rand, RECTANGLE_MIN_HEIGHT, Math.min(RECTANGLE_MAX_HEIGHT, HEIGHT - base.y()));
        return new Rectangle(width, height, base);
    }

}
