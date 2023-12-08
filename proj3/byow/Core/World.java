package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {
    public static final long SEED = 123123;
    public static final Random RANDOM = new Random(SEED);
    public TETile[][] map = new TETile[Engine.WIDTH][Engine.HEIGHT];
    public List<Space> spaceList;
    public List<Door> doorList;
    public List<Door> proposedDoors;
    public List<Wall> walls;

    public World() {
        this.spaceList = new ArrayList<>();
        this.doorList = new ArrayList<>();
        this.proposedDoors = new ArrayList<>();
        this.walls = new ArrayList<>();
        for (int x = 0; x < Engine.WIDTH; x += 1) {
            for (int y = 0; y < Engine.HEIGHT; y += 1) {
                this.map[x][y] = Tileset.NOTHING;
            }
        }
    }
    public void drawAllDoors() {
        for (Door d : proposedDoors) {
            d.draw();
            d.setID(doorList.size());
            doorList.add(d);
        }
        proposedDoors.clear();
    }
    public void placeRandomRooms() {
        int randomRooms = 5 + RANDOM.nextInt(5);
        Room.generateRooms(this, randomRooms);
        for (Space room: spaceList) {
            Position randomPos = Position.randomPosition();
            while (!room.checkPosition(room, randomPos)) {
                randomPos = Position.randomPosition();
            }
            room.setPosition(randomPos);
            room.draw();
        }
    }
    public Space getSpaceByID(int id) {
        return spaceList.get(id);
    }
    public Door getDoorByID(int id) {
        return doorList.get(id);
    }

    public void generateRandomHallway() {
        int rand = RANDOM.nextInt(spaceList.size());
        Space randSpace = spaceList.get(rand);
        if (randSpace.height() > 3 && randSpace.width() > 3) {
            Door randDoor = randSpace.findDoor();
            if (randDoor != null) {
                int rand2 = 3+ RANDOM.nextInt( Engine.HEIGHT / 2);
                Space randHall = randDoor.buildHallway(rand2);
                randHall.draw();
            }
        }
    }
    public static void main(String[] args) {
        World w = new World();
        w.placeRandomRooms();
        System.out.println(TETile.toString(w.map));
        System.out.println(w.spaceList);
        w.generateRandomHallway();
        System.out.println(TETile.toString(w.map));
        System.out.println(w.spaceList);
        System.out.println(w.doorList);
        for (int i = 0; i < 394; i++) {
            w.generateRandomHallway();
        }
        System.out.println(TETile.toString(w.map));
        System.out.println(w.spaceList);
        System.out.println(w.doorList);
        w.generateRandomHallway();
        System.out.println(TETile.toString(w.map));

    }
}
