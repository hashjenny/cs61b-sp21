package byow.Core;

import byow.TileEngine.TETile;

import static byow.Core.Engine.HEIGHT;
import static byow.Core.Engine.WIDTH;

public record Point(int x, int y) {
    public boolean isValid(TETile[][] world) {
        return x >= 0 && y >= 0 && x < WIDTH && y < HEIGHT && world[x][y].character() == ' ';
    }
}
