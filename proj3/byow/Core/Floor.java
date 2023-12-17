package byow.Core;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Floor extends Component {
    private static final TETile R = Tileset.FLOOR;
    public Floor(Position position, Space parent) {
        super(position, parent);
        this.setRep(R);
    }
}
