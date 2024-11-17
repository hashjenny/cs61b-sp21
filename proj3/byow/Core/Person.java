package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Person {
    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPos(Point p) {
        setX(p.x());
        setY(p.y());
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Person(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Person(Point p) {
        new Person(p.x(), p.y());
    }

    public void move(TETile[][] world, int posX, int posY) {
        if (world[posX][posY].character() == '·') {
            world[x][y] = Tileset.FLOOR;
            x = posX;
            y = posY;
            world[x][y] = Tileset.AVATAR;
        }
    }


}
