package byow.Core;

public class ut {
    public enum Direction {
        NORTH,
        SOUTH,
        WEST,
        EAST
    }
    public static Direction clockwise(Direction d) {
        Direction res = null;
        switch(d) {
            case NORTH: res = Direction.EAST;
            case EAST: res = Direction.SOUTH;
            case SOUTH: res = Direction.WEST;
            case WEST: res = Direction.NORTH;
        }
        return res;
    }
    public static Direction counterclock(Direction d) {
        Direction res = null;
        switch(d) {
            case NORTH: res = Direction.WEST;
            case WEST: res = Direction.SOUTH;
            case SOUTH: res = Direction.EAST;
            case EAST: res = Direction.NORTH;
        }
        return res;
    }
    public static Direction inverse(Direction d) {
        Direction res = null;
        switch(d) {
            case NORTH: res = Direction.SOUTH;
            case WEST: res = Direction.EAST;
            case SOUTH: res = Direction.NORTH;
            case EAST: res = Direction.WEST;
        }
        return res;
    }

    public static int randNum(int start, int end) {
        int res = start + World.RANDOM.nextInt(end - start);
        return res;
    }

}
