package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Wall {
    private Space parent;
    private static final TETile representation = Tileset.WALL;
    public Wall(Space parent) {
        this.parent = parent;
    }
    public TETile build() {
        return representation;
    }
    public Space parent() {
        return parent;
    }
}
