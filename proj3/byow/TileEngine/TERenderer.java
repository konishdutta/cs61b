package byow.TileEngine;

import byow.Core.*;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utility class for rendering tiles. You do not need to modify this file. You're welcome
 * to, but be careful. We strongly recommend getting everything else working before
 * messing with this renderer, unless you're trying to do something fancy like
 * allowing scrolling of the screen or tracking the avatar or something similar.
 */
public class TERenderer {
    private static final int TILE_SIZE = 16;
    private static final int MIN_FLICKER = 70;
    private int width;
    private int height;
    private int xOffset;
    private int yOffset;
    private boolean[][] isFov;
    private List<LightIntensity>[][] lightGrid;
    private LightBlend[][] finalLightGrid;
    private LightBlend[][] origLightGrid;
    private int currFlicker = 100;
    private double[][] fov;

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
                double distance = calculateDistance(x, y, ax, ay);
                double darkeningFactor = calculateDarkeningFactor(distance, lightRadius);
                TETile litTile = world[x][y];
                litTile = litTile.applyLightingEffect(darkeningFactor);
                litTile.draw(x + xOffset, y + yOffset);
            }
        }
        StdDraw.show();
    }

    public void renderRayLight(TETile[][] world, World w, int lightRadius) {
        this.isFov = new boolean[width][height];
        this.fov = new double[width][height];
        Avatar a = w.avatar();
        Position p = a.position();
        prepLights(w);
        blendLights();
        flicker();
        generateFov2(w, lightRadius);
        //generateFov(w, lightRadius);

        int ax = p.x();
        int ay = p.y();
        int numXTiles = world.length;
        int numYTiles = world[0].length;
        StdDraw.clear(new Color(0, 0, 0));

        for (int x = 0; x < numXTiles; x++) {
            for (int y = 0; y < numYTiles; y++) {
                Position pos = new Position(x, y);
                Component c = w.getComponentByPosition(pos);

                if (fov[x][y] != 0) {
                    double intensity = 0;
                    if (finalLightGrid[x][y] != null) {
                        intensity = finalLightGrid[x][y].intensity();
                    }
                    TETile litTile = world[x][y];
                    if (finalLightGrid[x][y] != null) {
                        LightBlend lb = finalLightGrid[x][y];
                        litTile = TETile.blendLight(world[x][y], lb);
                    }
                    double distance = calculateDistance(x, y, ax, ay);
                    double lightMultiplier = fov[x][y];
                    litTile = litTile.applyLightingEffect(lightMultiplier);
                    //double darkeningFactor = calculateDarkeningFactor(distance, lightRadius);
                    //litTile = litTile.applyLightingEffect(darkeningFactor - intensity);
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
    public void flicker() {
        Random random = new Random();
        // first randomly roll 1/6 chance of not flickering.
        int stay = random.nextInt(6);
        if (stay == 0) {
            return;
        }
        // pick a random int between min flicker and 100
        // this approach makes it more likely to go down when you are closer to the max
        int dice = MIN_FLICKER + random.nextInt(100 - MIN_FLICKER);
        double flicker = (double) (random.nextInt(6)) / 100;
        if (dice == currFlicker) {
            return;
        }
        // if the dice is less than the curr flicker level, lessen it.
        if (dice < currFlicker) {
            flicker *= -1;
        }
        currFlicker += (int) (flicker * 100);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                LightBlend lightBlend = finalLightGrid[x][y];
                LightBlend origBlend = origLightGrid[x][y];
                if (lightBlend != null) {
                    // Apply a random flicker within a defined range
                    double newIntensity = Math.max((MIN_FLICKER / 100),
                            Math.min(Math.max(0.75, origBlend.intensity()),
                                    lightBlend.intensity() + flicker));
                    finalLightGrid[x][y] = new LightBlend(newIntensity, lightBlend.color());
                }
            }
        }
    }

    public void generateFov2(World w, int lightRadius) {
        double angleIncrement = 0.000125 * Math.PI;
        Position p = w.avatar().position();
        int ax = p.x();
        int ay = p.y();
        for (double angle = 0; angle < 2 * Math.PI; angle += angleIncrement) {
            double dx = Math.cos(angle);
            double dy = Math.sin(angle);
            castFov2(w, ax, ay, dx, dy);
        }
    }
    private void castFov2(World w, int startX, int startY, double dx, double dy) {
        double x = startX;
        double y = startY;
        int gridX = (int) Math.round(x);
        int gridY = (int) Math.round(y);
        Position curr = new Position(gridX, gridY);
        double lightFactor = 1.5;

        while (!curr.outOfBounds()) {

            double distance = calculateDistance(gridX, gridY, startX, startY);
            //if distance = 0, just get out of loop to avoid zero division
            if (distance == 0) {
                fov[gridX][gridY] = lightFactor;
            } else {
                double distanceMultiplier = lightFactor / (1 + 0.5 * (Math.pow(distance, 2)));
                if (distanceMultiplier < 0.001) {
                    distanceMultiplier = 0;
                }
                double lightSourceMultiplier = 0;
                if (finalLightGrid[gridX][gridY] != null) {
                    double intensity = finalLightGrid[gridX][gridY].intensity();
                    double a = 1; // Example value, adjust as needed\
                    lightSourceMultiplier = Math.exp(Math.PI * intensity) / ((10 + distance));
                    if (distance > 20) {
                        lightSourceMultiplier *= (160000 / Math.pow(distance, 4));
                    }
                    /* other ideas I tried for lightSourceMultiplier
                    * Math.min(1.5, Math.exp(10 * intensity) /
                    * Math.pow((1 + Math.PI * distance), 2));
                    * (10 * lightSourceIntensity / (1 + 0.5 * Math.pow(distance, 2)));
                     */
                }
                // Combine the distance and light source multipliers
                fov[gridX][gridY] = Math.max(distanceMultiplier, lightSourceMultiplier);
            }
            // if you hit a wall, exit
            if (w.getComponentByPosition(curr) instanceof Wall
                    && !(w.getComponentByPosition(curr) instanceof Door)) {
                break;
            }

            int tempX = gridX;
            int tempY = gridY;
            while (tempX == gridX && tempY == gridY) {
                x += dx;
                y += dy;
                gridX = (int) Math.round(x);
                gridY = (int) Math.round(y);
            }
            curr = new Position(gridX, gridY);
        }
    }



    public void prepLights(World w) {
        this.lightGrid = new List[width][height];
        this.finalLightGrid = new LightBlend[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                this.lightGrid[i][j] = new ArrayList<LightIntensity>();
            }
        }

        for (LightSource g : w.getGold()) {
            LightIntensity li = new LightIntensity(g, 1);
            lightGrid[g.position().x()][g.position().y()].add(li);
            double angleIncrement = 0.00125 * Math.PI;
            for (double angle = 0; angle < 2 * Math.PI; angle += angleIncrement) {
                double dx = Math.cos(angle);
                double dy = Math.sin(angle);
                generateIntensity(w, g, dx, dy);
            }
        }
    }
    public void generateIntensity(World w, LightSource ls, double dx, double dy) {
        double currIntensity = ls.getPower();
        int currX = (int) (ls.position().x());
        int currY = (int) (ls.position().y());
        while (currIntensity > 0.01) {
            double addX = currX;
            double addY = currY;
            int newX = currX;
            int newY = currY;
            while (newX == currX && newY == currY) {
                addX = addX + dx;
                addY = addY + dy;
                newX = (int) addX;
                newY = (int) addY;
            }
            currX = newX;
            currY = newY;


            double distance = calculateDistance(currX, currY,
                    ls.position().x(), ls.position().y());
            LightIntensity li = new LightIntensity(ls, distance);
            Position candidatePosition = new Position(currX, currY);
            if (candidatePosition.outOfBounds()) {
                return;
            }
            if (!lightGrid[currX][currY].contains(li)) {
                lightGrid[currX][currY].add(li);
            }
            if (w.getComponentByPosition(candidatePosition).wall()
                    && !w.getComponentByPosition(candidatePosition).door()) {
                return;
            }
            currIntensity = li.intensity();
        }
    }
    public void blendLights() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (lightGrid[i][j].isEmpty()) {
                    continue;
                }
                double totalIntensity = 0;
                double weightedR = 0;
                double weightedG = 0;
                double weightedB = 0;
                for (LightIntensity li : lightGrid[i][j]) {
                    double liIntensity = li.intensity();
                    totalIntensity += liIntensity;
                    weightedR += li.source().r() * liIntensity;
                    weightedG += li.source().g() * liIntensity;
                    weightedB += li.source().b() * liIntensity;
                }
                if (totalIntensity > 0) {
                    int r = (int) (weightedR / totalIntensity);
                    int g = (int) (weightedG / totalIntensity);
                    int b = (int) (weightedB / totalIntensity);
                    LightBlend blend = new LightBlend(totalIntensity, new Color(r, g, b));
                    finalLightGrid[i][j] = blend;
                }
            }
        }
        origLightGrid = finalLightGrid;
    }

}
