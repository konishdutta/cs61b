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
            break;
            case EAST: res = Direction.SOUTH;
            break;
            case SOUTH: res = Direction.WEST;
            break;
            case WEST: res = Direction.NORTH;
            break;
        }
        return res;
    }
    public static Direction counterclock(Direction d) {
        Direction res = null;
        switch(d) {
            case NORTH: res = Direction.WEST;
            break;
            case WEST: res = Direction.SOUTH;
            break;
            case SOUTH: res = Direction.EAST;
            break;
            case EAST: res = Direction.NORTH;
            break;
        }
        return res;
    }
    public static Direction inverse(Direction d) {
        Direction res = null;
        switch(d) {
            case NORTH: res = Direction.SOUTH;
            break;
            case WEST: res = Direction.EAST;
            break;
            case SOUTH: res = Direction.NORTH;
            break;
            case EAST: res = Direction.WEST;
            break;
        }
        return res;
    }


}
