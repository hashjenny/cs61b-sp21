package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;
import java.util.Objects;

import static byow.Core.Engine.*;

public final class Point implements Comparable<Point> {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isValid(TETile[][] world) {
        return x >= 0 && y >= 0 && x < WIDTH && y < HEIGHT && world[x][y].character() == ' ';
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Point) obj;
        return this.x == that.x &&
                this.y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Point[" +
                "x=" + x + ", " +
                "y=" + y + ']';
    }

    public static void drawHall(TETile[][] world, Point p) {
        drawHall(world, p.x, p.y);
    }

    public static void drawHall(TETile[][] world, int x, int y) {
        world[x][y] = HALL;
    }


    @Override
    public int compareTo(Point p) {
        if (x > p.x) {
            return 1;
        } else if (x < p.x) {
            return -1;
        } else {
            return Integer.compare(y, p.y);
        }
    }

    public boolean isNear(Point p) {
        int px = p.x();
        int py = p.y();
        return (x == px && Math.abs(y - py) == 1)
                || (y == py && Math.abs(x - px) == 1);
    }
}
