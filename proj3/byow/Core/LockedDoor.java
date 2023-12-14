package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class LockedDoor extends Door {
    private static final TETile r = Tileset.LOCKED_DOOR;
    public LockedDoor(Wall w) {
        super(w);
        this.setRep(r);
    }
}
