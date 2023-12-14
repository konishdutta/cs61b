package byow.Core;

import byow.TileEngine.TETile;

import java.awt.*;

public class LightSource extends Floor{
    private int power;
    private int red;
    private int green;
    private int blue;
    private static final TETile r = new TETile('·', Color.LIGHT_GRAY, Color.WHITE,
            "light source");

    public LightSource(Position position, Space parent, int red, int green, int blue, int power) {
        super(position, parent);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.power = power;
        this.setRep(r);
    }

    public int getPower() {
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
