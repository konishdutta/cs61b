package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.*;

public class World {
    public long SEED;
    public Random RANDOM;
    public TETile[][] map = new TETile[Engine.WIDTH][Engine.HEIGHT];
    public List<Space> spaceList;
    public Set<Door> doors;
    public Set<Wall> walls;
    public HashMap<Position, Component> pc;

    public World(long SEED) {
        this.SEED = SEED;
        this.RANDOM = new Random(SEED);
        this.spaceList = new ArrayList<>();
        this.doors = new HashSet<>();
        this.walls = new HashSet<>();
        this.pc = new HashMap<>();
        for (int x = 0; x < Engine.WIDTH; x += 1) {
            for (int y = 0; y < Engine.HEIGHT; y += 1) {
                Position p = new Position(x, y);
                Nothing n = new Nothing(p, this);
                build(n);
            }
        }
    }
    public void build(Component c) {
        Position p = c.position();
        map[p.x()][p.y()] = c.rep();
        pc.put(p, c);
    }

    public void addWall(Wall w) {
        walls.add(w);
    }
    public void addDoor(Door d) {
        doors.add(d);
    }
    public int randNum(int start, int end) {
        int res = start + RANDOM.nextInt(end - start);
        return res;
    }

    public void removeWall(Wall w) {
        walls.remove(w);
    }
    public void generateRandomRooms() {
        int randomRooms = randNum(5, 10);
        for (int i = 0; i < randomRooms; i++) {
            int randHeight =  randNum(5, Engine.HEIGHT / 3);
            int randWidth =  randNum(5, Engine.WIDTH / 3);
            Position randomPos = randomSafePosition(randHeight, randWidth);
            if (randomPos != null) {
                Room room = new Room(this, randHeight, randWidth);
                room.setPosition(randomPos);
                room.draw();
            }
        }
    }
    public Position randomSafePosition(int height, int width) {
        boolean res = false;
        Position randPosition = null;
        int i = 0;
        while (!res && i < 5) {
            randPosition = new Position(
                    randNum(0, Engine.WIDTH - width),
                    randNum(0, Engine.HEIGHT - height));
            res = checkRoomCanFit(height, width, randPosition);
            i += 1;
        }
        if (i == 5) {
            return null;
        }
        return randPosition;
    }
    public boolean checkRoomCanFit(int height, int width, Position p) {
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                Position candidate = new Position(p.x() + w, p.y() + h);
                if (!(pc.get(candidate) instanceof Nothing)) {
                    return false;
                }
            }
        }
        return true;
    }
    public Space getSpaceByPosition(Position p) {
        Component c = pc.get(p);
        return c.parent();
    }
    public Component getComponentByPosition(Position p) {
        Component c = pc.get(p);
        return c;
    }
    public Space getSpaceByID(int id) {
        return spaceList.get(id);
    }
    public void generateRandomDoor() {
        // look for a door that allows us to build a hallway
        // we'll do this in two steps
        int maxWidth = 0;
        Wall candidate = null;
        ut.Direction candidateDirection = null;
        List<Wall> wallList= new ArrayList<>(walls);
        while (maxWidth == 0) {
            candidateDirection = null;
            //step 1) find a wall that has a potentially working direction
            // this means finding a wall with only 1 adjoining floor that is behind you
            while (candidateDirection == null) {
                int randNum = randNum(0, wallList.size());
                candidate = wallList.get(randNum);
                candidateDirection = candidate.doorDirection();
            }
            // now you have a direction. we need to pressure test the direction
            // you need to test that the position in front of the wall is OK
            // -2 = 2 double doors, -1 = 1 double door, 0 = not ok, 1, 2 means build a hallway
            maxWidth = candidate.probeForHallSpan(candidateDirection, 2);
        }
        Door newDoor = new Door(candidate);
        if (maxWidth < 0) {
            newDoor.doubleDoor(candidateDirection, maxWidth * -1);
        } else {
            Hallway res = newDoor.launchHallway(candidateDirection, maxWidth, 0);
            res.draw();
            //build the doors
            Position[] wallData = res.returnWallStarts();
            res.bulldozeForDoors(wallData);
            for (Door d: doors) {
                //System.out.println(TETile.toString(map));
                //System.out.println(d);
                build(d);
            }
        }
    }

    public void curatedHallway() {
        int id = 0;
        Set<Space> visited = getSpaceByID(id).dfs();
        Hallway cHall = null;
        for (Space s : visited) {
            cHall = s.checkWallsForHalls(visited);
            if (cHall != null) {
                cHall.draw();
                //build the doors
                Position[] wallData = cHall.returnWallStarts();
                cHall.bulldozeForDoors(wallData);
                for (Door d : doors) {
                    build(d);
                }
                break;
            }

        }
    }

    public Space neighbor(Position p, ut.Direction d) {
        Position curr = p;
        Space res = null;
        Space origin = getComponentByPosition(curr).parent();
        while (!curr.outOfBounds() && (origin == null ||
                getComponentByPosition(curr).parent() == null ||
                origin.equals(getComponentByPosition(curr).parent()))) {
            curr = curr.moveDirection(d);
        }
        if (curr.outOfBounds()) {
            return null;
        }
        res = getComponentByPosition(curr).parent();
        return res;
    }

    public TETile[][] randomLayout() {
        generateRandomRooms();
        int randHallCount = randNum(5, 30);
        for (int i = 0; i < randHallCount; i++) {
            generateRandomDoor();
        }
        while (spaceList.get(0).dfs().size() != spaceList.size()) {
            int currSize = spaceList.size();
            curatedHallway();
            if (spaceList.size() == currSize) {
                generateRandomDoor();
            }
        }
        return map;
    }


    public static void main(String[] args) {
        World w = new World(1223);
        w.randomLayout();
        System.out.println(TETile.toString(w.map));
    }
}
