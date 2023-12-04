package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 60;
    private static final int HEIGHT = 30;

    public static void addHexagon(TETile[][] world, int s) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        int hexHeight = s * 2;
        int hexWidth = (s * 2) + (s - 2);
        for (int y = 0; y < hexHeight; y += 1) {
            hexConstructor(world, hexWidth, 0, y, s);
        }
    }
    private static void hexConstructor(TETile[][] world, int hexWidth, int x, int y, int s) {
        /* calculate an offset where you draw blank tiles */
        int offset = y;
        if (y > s - 1) {
            offset = (s - 1) + (s - y);
        }
        for (int j = 0; j < hexWidth; j += 1) {
            if (j < s - offset - 1 || hexWidth - j - 1 < s - offset - 1) {
                world[j + x][y] = Tileset.NOTHING;
            } else {
                world[j + x][y] = Tileset.FLOWER;
            }
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        addHexagon(world, 5);

        ter.renderFrame(world);
    }

}