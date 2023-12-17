package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Nothing extends Component {
    private static final TETile R = Tileset.NOTHING;
    public Nothing(Position position, World world) {
        super(position, world);
        this.setRep(R);
    }
}
