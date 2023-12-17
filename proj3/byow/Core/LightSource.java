package byow.Core;

import byow.TileEngine.TETile;

import java.awt.*;

public class LightSource extends Floor {
    private double power; //should be in [0, 1]
    private int red;
    private int green;
    private int blue;

    public LightSource(Position position, Space parent,
                       int red, int green, int blue,
                       double power) {
        super(position, parent);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.power = power;
        Color tileColor = new Color(Math.max(0, Math.min(255, (int) (red * power))),
                Math.max(0, Math.min(255, (int) (green * power))),
                Math.max(0, Math.min(255, (int) (blue * power))));
        TETile r = new TETile('·', Color.WHITE, tileColor,
                "light source");
        this.setRep(r);
    }

    public double getPower() {
        return this.power;
    }
    public int r() {
        return this.red;
    }
    public int g() {
        return this.green;
    }
    public int b() {
        return this.blue;
    }

}
