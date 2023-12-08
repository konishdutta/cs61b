package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Component {
    private Space parent;
    private Position position;
    private TETile representation;
    public Component(Position position, Space parent) {
        this.parent = parent;
        this.position = position;
    }
    public Component(Position position) {
        this(position, null);
    }
    public TETile build() {
        return representation;
    }
    public Space parent() {
        return parent;
    }
}
