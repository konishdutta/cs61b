package byow.TileEngine;

import byow.Core.*;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for rendering tiles. You do not need to modify this file. You're welcome
 * to, but be careful. We strongly recommend getting everything else working before
 * messing with this renderer, unless you're trying to do something fancy like
 * allowing scrolling of the screen or tracking the avatar or something similar.
 */
public class TERenderer {
    private static final int TILE_SIZE = 16;
    private int width;
    private int height;
    private int xOffset;
    private int yOffset;
    private boolean[][] isFov;
    private double [][] lightGrid;

    /**
     * Same functionality as the other initialization method. The only difference is that the xOff
     * and yOff parameters will change where the renderFrame method starts drawing. For example,
     * if you select w = 60, h = 30, xOff = 3, yOff = 4 and then call renderFrame with a
     * TETile[50][25] array, the renderer will leave 3 tiles blank on the left, 7 tiles blank
     * on the right, 4 tiles blank on the bottom, and 1 tile blank on the top.
     * @param w width of the window in tiles
     * @param h height of the window in tiles.
     */
    public void initialize(int w, int h, int xOff, int yOff) {
        this.width = w;
        this.height = h;
        this.xOffset = xOff;
        this.yOffset = yOff;
        StdDraw.setCanvasSize(width * TILE_SIZE, height * TILE_SIZE);
        Font font = new Font("Monaco", Font.BOLD, TILE_SIZE - 2);
        StdDraw.setFont(font);      
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);

        StdDraw.clear(new Color(0, 0, 0));

        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }

    /**
     * Initializes StdDraw parameters and launches the StdDraw window. w and h are the
     * width and height of the world in number of tiles. If the TETile[][] array that you
     * pass to renderFrame is smaller than this, then extra blank space will be left
     * on the right and top edges of the frame. For example, if you select w = 60 and
     * h = 30, this method will create a 60 tile wide by 30 tile tall window. If
     * you then subsequently call renderFrame with a TETile[50][25] array, it will
     * leave 10 tiles blank on the right side and 5 tiles blank on the top side. If
     * you want to leave extra space on the left or bottom instead, use the other
     * initializatiom method.
     * @param w width of the window in tiles
     * @param h height of the window in tiles.
     */
    public void initialize(int w, int h) {
        initialize(w, h, 0, 0);
    }

    /**
     * Takes in a 2d array of TETile objects and renders the 2d array to the screen, starting from
     * xOffset and yOffset.
     *
     * If the array is an NxM array, then the element displayed at positions would be as follows,
     * given in units of tiles.
     *
     *              positions   xOffset |xOffset+1|xOffset+2| .... |xOffset+world.length
     *                     
     * startY+world[0].length   [0][M-1] | [1][M-1] | [2][M-1] | .... | [N-1][M-1]
     *                    ...    ......  |  ......  |  ......  | .... | ......
     *               startY+2    [0][2]  |  [1][2]  |  [2][2]  | .... | [N-1][2]
     *               startY+1    [0][1]  |  [1][1]  |  [2][1]  | .... | [N-1][1]
     *                 startY    [0][0]  |  [1][0]  |  [2][0]  | .... | [N-1][0]
     *
     * By varying xOffset, yOffset, and the size of the screen when initialized, you can leave
     * empty space in different places to leave room for other information, such as a GUI.
     * This method assumes that the xScale and yScale have been set such that the max x
     * value is the width of the screen in tiles, and the max y value is the height of
     * the screen in tiles.
     * @param world the 2D TETile[][] array to render
     */
    public void renderFrame(TETile[][] world) {
        int numXTiles = world.length;
        int numYTiles = world[0].length;
        StdDraw.clear(new Color(0, 0, 0));
        for (int x = 0; x < numXTiles; x += 1) {
            for (int y = 0; y < numYTiles; y += 1) {
                if (world[x][y] == null) {
                    throw new IllegalArgumentException("Tile at position x=" + x + ", y=" + y
                            + " is null.");
                }
                world[x][y].draw(x + xOffset, y + yOffset);
            }
        }
        StdDraw.show();
    }

    public void renderSimpleLight(TETile[][] world, World w, int lightRadius) {
        Avatar a = w.avatar();
        Position p = a.position();
        int ax = p.x();
        int ay = p.y();
        int numXTiles = world.length;
        int numYTiles = world[0].length;
        StdDraw.clear(new Color(0, 0, 0));

        for (int x = 0; x < numXTiles; x++) {
            for (int y = 0; y < numYTiles; y++) {
                if (world[x][y] == null) {
                    throw new IllegalArgumentException("Tile at position x=" + x + ", y=" + y + " is null.");
                }
                double distance = calculateDistance(x, y, ax, ay);
                double darkeningFactor = calculateDarkeningFactor(distance, lightRadius);
                TETile litTile = TETile.applyLightingEffect(world[x][y], darkeningFactor);
                litTile.draw(x + xOffset, y + yOffset);
            }
        }
        StdDraw.show();
    }

    public void renderRayLight(TETile[][] world, World w, int lightRadius) {
        this.isFov = new boolean[width][height];
        Avatar a = w.avatar();
        Position p = a.position();
        generateFov(w, lightRadius);

        int ax = p.x();
        int ay = p.y();
        int numXTiles = world.length;
        int numYTiles = world[0].length;
        StdDraw.clear(new Color(0, 0, 0));

        for (int x = 0; x < numXTiles; x++) {
            for (int y = 0; y < numYTiles; y++) {
                if (world[x][y] == null) {
                    throw new IllegalArgumentException("Tile at position x=" + x + ", y=" + y + " is null.");
                }
                Position pos = new Position(x, y);
                Component c = w.getComponentByPosition(pos);

                if (isFov[x][y]) {
                    double distance = calculateDistance(x, y, ax, ay);
                    double darkeningFactor = calculateDarkeningFactor(distance, lightRadius);
                    TETile litTile = TETile.applyLightingEffect(world[x][y], darkeningFactor);
                    litTile.draw(x + xOffset, y + yOffset);
                }
            }
        }
        StdDraw.show();
    }
    private double calculateDistance(int x1, int y1, int x2, int y2) {
        // Implement distance calculation (Euclidean)
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
    private double calculateDarkeningFactor(double distance, int lightRadius) {
        // Exponential decay factor
        double decayFactor = 1;
        double darkening = 1 - Math.exp(-decayFactor * distance / lightRadius);
        return Math.min(1, Math.max(0, darkening));
    }

    public void generateFov(World w, int lightRadius) {
        double angleIncrement = 0.0125 * Math.PI;
        Position p = w.avatar().position();
        System.out.println(p);
        int ax = p.x();
        int ay = p.y();
        for (double angle = 0; angle < 2 * Math.PI; angle += angleIncrement) {
            double dx = Math.cos(angle);
            double dy = Math.sin(angle);
            castFov(w, ax, ay, dx, dy, lightRadius);
        }
    }

    public void prepLights(World w) {
        w.getGold();

    }

    private void castFov(World w, int startX, int startY, double dx, double dy, int lightRadius) {
        double x = startX, y = startY;
        double distanceTraveled = 0;
        while (distanceTraveled < lightRadius) {
            int gridX = (int) Math.round(x);
            int gridY = (int) Math.round(y);
            Position p = new Position(gridX, gridY);
            if (p.outOfBounds()) {
                break;
            }
            if (w.getComponentByPosition(p).wall() && !w.getComponentByPosition(p).door()) {
                isFov[gridX][gridY] = true;
                return;
            }
            // Mark the tile as lit
            isFov[gridX][gridY] = true;
            x += dx;
            y += dy;
            distanceTraveled = calculateDistance(gridX, gridY, startX, startY);
        }
    }

}
