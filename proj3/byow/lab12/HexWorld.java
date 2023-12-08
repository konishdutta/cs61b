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
    private static final int WIDTH = 80;
    private static final int HEIGHT = 50;
    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);


    public static void tesHex(TETile[][] world, int s) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        int tesHeight = s * 2 * 5;
        for (int j = 0; j < 5; j += 1) {
            int xHex = s * 2 * j - j;
            int offY = Math.min(j, 4 - j);
            int yHex = s * 2 - offY * s;
            stackHex(world, s, xHex, yHex, 3 + offY);
        }
    }
    private static void stackHex(TETile[][] world, int s, int x, int y, int count) {
        for (int c = 0; c < count; c += 1) {
            int offset = c * 2 * s;
            singleHex(world, s, x, y + offset, randomTile());
        }
    }

    public static void addHexagon(TETile[][] world, int s) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        int hexHeight = s * 2;
        int hexWidth = (s * 2) + (s - 2);
        for (int y = 0; y < hexHeight; y += 1) {
            hexConstructor(world, hexWidth, 0, 0, y, s, Tileset.FLOWER);
        }
    }
    private static void singleHex(TETile[][] world, int s, int x, int y, TETile tile) {
        int hexHeight = s * 2;
        int hexWidth = (s * 2) + (s - 2);
        for (int j = 0; j < hexHeight; j += 1) {
            hexConstructor(world, hexWidth, x, y, j, s, tile);
        }
    }
    private static void hexConstructor(TETile[][] world, int hexWidth, int x, int y, int row, int s, TETile tile) {
        /* calculate an offset where you draw blank tiles */
        int offset = Math.min(row, (s - 1) + (s - row));
        System.out.println(offset);
        for (int j = 0; j < hexWidth; j += 1) {
            if (j >= s - offset - 1 && hexWidth - j - 1 >= s - offset - 1) {
                world[j + x][row + y] = tile;
            }
        }
    }
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(6);
        switch (tileNum) {
            case 0: return Tileset.SAND;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.MOUNTAIN;
            case 3: return Tileset.TREE;
            case 4: return Tileset.GRASS;
            case 5: return Tileset.WATER;
            default: return Tileset.SAND;
        }
    }
    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        tesHex(world, 5);

        ter.renderFrame(world);
    }

}