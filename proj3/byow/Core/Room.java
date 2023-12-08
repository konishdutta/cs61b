package byow.Core;
import byow.TileEngine.Tileset;

import java.util.Random;

public class Room extends Space {
    public Room(World w, int height, int width) {
        super(w, height, width);
    }


    public static void generateRooms(World w, int x) {
        for (int i = 0; i < x; i++) {
            int randHeight =  5 + World.RANDOM.nextInt(Engine.HEIGHT / 4);
            int randWidth =  5 + World.RANDOM.nextInt(Engine.HEIGHT / 4);
            new Room(w, randHeight, randWidth);
        }
    }

}
