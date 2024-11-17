package byow.Core;

import byow.TileEngine.TETile;

import java.util.*;

import static byow.Core.Engine.*;

public class Rectangle {
    private final int width;
    private final int height;
    private final Point basePoint;
    private final ArrayList<Point> points;
    private final HashSet<Point> outerPoints;
    private final HashSet<Point> innerPoints;
    private final HashMap<Point, Point> pairs;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Point getBasePoint() {
        return basePoint;
    }

    public Point getStartPoint() {
        return new Point(basePoint.x() + 1, basePoint.y() + 1);
    }

    public HashMap<Point, Point> getPairs() {
        return pairs;
    }

    public Rectangle(int width, int height, Point p) {
        this.width = width;
        this.height = height;
        this.basePoint = p;
        var x = basePoint.x();
        var y = basePoint.y();

        this.points = new ArrayList<>();
        this.outerPoints = new HashSet<>();
        this.innerPoints = new HashSet<>();

        this.pairs = new HashMap<>();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                var point = new Point(x + i, y + j);
                points.add(point);
                if (i == 0 || i == width - 1
                        || j == 0 || j == height - 1) {
                    outerPoints.add(point);
                } else {
                    innerPoints.add(point);
                }
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (Rectangle) obj;
        return this.width == that.width
                && this.height == that.height
                && this.basePoint.equals(that.basePoint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, basePoint);
    }

    public boolean isValid(TETile[][] world) {
        for (Point p : points) {
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

    public void draw(TETile[][] world) {
        // for (Point p : outerPoints) {
        //     world[p.x()][p.y()] = Tileset.WALL;
        // }
        // for (Point p : innerPoints) {
        //     world[p.x()][p.y()] = Tileset.WATER;
        // }
        for (var p : points) {
            Point.drawWall(world, p);
        }
    }

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

    // 获取每个相邻的矩形的2个邻接点
    public void addNearPair(Rectangle rect2) {
        HashMap<Point, Point> map = new HashMap<>();
        for (Point p1 : outerPoints) {
            for (Point p2 : rect2.outerPoints) {
                if (p1.isNear(p2)) {
                    map.put(p1, p2);
                }
            }
        }
        var minPoint = Collections.min(map.keySet());
        var maxPoint = Collections.max(map.keySet());
        map.remove(minPoint);
        map.remove(maxPoint);

        var times = 2;
        for (var entry : map.entrySet()) {
            if (times > 0) {
                pairs.put(entry.getKey(), entry.getValue());
                rect2.getPairs().put(entry.getValue(), entry.getKey());
                times--;
            } else {
                break;
            }
        }
    }

    public Point getNearInnerPoint(Point p) {
        var x = p.x();
        var y = p.y();
        if (innerPoints.contains(new Point(x + 1, y))) {
            return new Point(x + 1, y);
        } else if (innerPoints.contains(new Point(x - 1, y))) {
            return new Point(x - 1, y);
        } else if (innerPoints.contains(new Point(x, y + 1))) {
            return new Point(x, y + 1);
        } else if (innerPoints.contains(new Point(x, y - 1))) {
            return new Point(x, y - 1);
        }
        return null;
    }
}
