package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Avatar extends Component {
    private static final TETile r = Tileset.AVATAR;
    public Avatar(Position position, World world) {
        super(position, world);
        this.setRep(r);
    }
}
